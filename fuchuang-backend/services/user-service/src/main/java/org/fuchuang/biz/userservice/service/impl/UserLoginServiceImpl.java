package org.fuchuang.biz.userservice.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.validation.ValidationUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.userservice.common.constant.RedisKeyConstant;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dao.mapper.UserMapper;
import org.fuchuang.biz.userservice.dto.req.UserLoginReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserSendCodeReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;
import org.fuchuang.biz.userservice.service.UserLoginService;
import org.fuchuang.biz.userservice.toolkit.MailUtil;
import org.fuchuang.framework.starter.cache.DistributedCache;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.fuchuang.frameworks.starter.user.core.UserInfoDTO;
import org.fuchuang.frameworks.starter.user.toolkit.JWTUtil;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * 用户登录接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserMapper userMapper;
    private final DistributedCache distributedCache;
    private final RedissonClient redissonClient;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 用户登录
     *
     * @param requestParam 用户登录参数
     * @return 用户基本信息
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 参数校验
        if (requestParam == null || StrUtil.isBlank(requestParam.getEmail()) || requestParam.getLoginType() == null || requestParam.getLoginType() < 0 || requestParam.getLoginType() > 2) {
            throw new ClientException("参数有误");
        }

        // 判断是验证码还是密码
        int loginType = requestParam.getLoginType();
        UserDO userDO;
        // todo: 替换为常量
        if (loginType == 0) {
            // 密码登录
            // 参数校验
            if (StrUtil.isBlank(requestParam.getPassword())) {
                throw new ClientException("密码不能为空");
            }
            // 根据邮箱查询用户信息
            userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                    .eq(UserDO::getEmail, requestParam.getEmail()));
            if (userDO == null) {
                throw new ClientException("用户不存在");
            }
            // 加盐比对，规则是 原始密码 + salt 再bcrypt哈希
            String getPassword = DigestUtil.md5Hex((requestParam.getPassword() + userDO.getSalt()));
            if (!getPassword.equals(userDO.getPassword())) {
                throw new ClientException("密码错误");
            }
        } else {
            // 验证码登录
            // 参数校验
            if (StrUtil.isBlank(requestParam.getCode())) {
                throw new ClientException("验证码不能为空");
            }
            // 根据邮箱查询用户信息
            userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                    .eq(UserDO::getEmail, requestParam.getEmail()));
            if (userDO == null) {
                throw new ClientException("用户不存在");
            }
            // 查询redis中的验证码
            StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
            // key = prefix + email
            String realCode = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_LOGIN_VERIFY_CODE + requestParam.getEmail());
            if (StrUtil.isBlank(realCode)) {
                throw new ClientException("验证码已过期");
            }
            if (!realCode.equals(requestParam.getCode())) {
                throw new ClientException("验证码错误");
            }

            // 到这里说明验证码正确，删除redis中的验证码
            stringRedisTemplate.delete(RedisKeyConstant.USER_LOGIN_VERIFY_CODE + requestParam.getEmail());
        }

        // 构建用户上下文
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(String.valueOf(userDO.getId()))
                .username(userDO.getUsername())
                .build();
        // 生成token
        String accessToken = JWTUtil.generateAccessToken(userInfoDTO);

        // 构造响应
        UserLoginRespDTO actual = UserLoginRespDTO.builder()
                .userId(String.valueOf(userDO.getId()))
                .username(userDO.getUsername())
                .accessToken(accessToken)
                .build();

        // 保存信息到到redis
        distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);

        // 返回响应
        return actual;
    }

    /**
     * 发送验证码
     *
     * @param requestParam 发送验证码请求参数
     */
    @Override
    public boolean sendVerifyCode(UserSendCodeReqDTO requestParam) {
        // 参数校验
        String email = requestParam.getEmail();
        if (StrUtil.isBlank(email)) {
            throw new ClientException("参数有误");
        }

        // 限流操作
        // 构造key
        String key = RedisKeyConstant.USER_SEND_CODE_LIMIT + email;
        // 限流
        // todo: 改为可配置
        doRateLimit(key, 1, 60, "验证码发送过于频繁，请稍后再试");

        // 发送验证码
        // todo: 使用MQ
        // 生成验证码
        String verifyCode = RandomUtil.randomNumbers(6);
        // 生成html模版
        Context context = new Context(); // 引入Template的Context
        // 设置模板中的变量（分割验证码）
        context.setVariable("verifyCode", Arrays.asList(verifyCode.split("")));
        // 第一个参数为模板的名称(html不用写全路径)
        String process = templateEngine.process("EmailVerificationCode.html", context); // 这里不用写全路径
        // 调用工具类发送
        MailUtil.sendMail(javaMailSender, fromEmail, email, process, true);

        // 在redis中保存
        distributedCache.put(RedisKeyConstant.USER_LOGIN_VERIFY_CODE + email, verifyCode, 60, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 限流操作
     *
     * @param key     限流key
     * @param limit   指定时间内允许的请求数
     * @param ttl     指定时间 单位秒
     * @param message 限流的提示信息
     */
    public void doRateLimit(String key, int limit, int ttl, String message) {
        // 创建限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, limit,
                ttl, RateIntervalUnit.SECONDS);
        // 获取令牌
        boolean result = rateLimiter.tryAcquire(1);
        if (!result) {
            throw new ServiceException(message);
        }
    }

    /**
     * 验证登录
     *
     * @param accessToken 用户登录 Token 凭证
     * @return 验证登录返回结果
     */
    @Override
    public UserLoginRespDTO checkLogin(String accessToken) {
        return distributedCache.get(accessToken, UserLoginRespDTO.class);
    }

    /**
     * 登出操作
     *
     * @param accessToken 用户登录 Token 凭证
     */
    @Override
    public void logout(String accessToken) {
        if (StrUtil.isNotBlank(accessToken)) {
            distributedCache.delete(accessToken);
        }
    }
}
