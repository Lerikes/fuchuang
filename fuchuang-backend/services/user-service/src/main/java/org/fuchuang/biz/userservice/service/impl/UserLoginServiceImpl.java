package org.fuchuang.biz.userservice.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.userservice.common.constant.RedisKeyConstant;
import org.fuchuang.biz.userservice.common.constant.UserConstant;
import org.fuchuang.biz.userservice.common.enums.UserChainMarkEnum;
import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dao.mapper.UserMapper;
import org.fuchuang.biz.userservice.dto.req.UserLoginReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserSendCodeReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;
import org.fuchuang.biz.userservice.service.UserLoginService;
import org.fuchuang.biz.userservice.toolkit.MailUtil;
import org.fuchuang.framework.starter.cache.DistributedCache;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.fuchuang.framework.starter.designpattern.chain.AbstractChainContext;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.fuchuang.frameworks.starter.user.core.UserInfoDTO;
import org.fuchuang.frameworks.starter.user.toolkit.JWTUtil;
import org.redisson.api.*;
import org.springframework.beans.BeanUtils;
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
public class UserLoginServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserLoginService{

    private final UserMapper userMapper;
    private final DistributedCache distributedCache;
    private final RedissonClient redissonClient;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${register.verify-code.limit}")
    private Integer verifyCodeLimit;
    @Value("${register.verify-code.ttl}")
    private Integer verifyCodeTtl;

    /**
     * 用户登录
     *
     * @param requestParam 用户登录参数
     * @return 用户基本信息
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 参数校验
        if (requestParam == null || StrUtil.isBlank(requestParam.getEmail()) ||
                requestParam.getLoginType() == null || requestParam.getLoginType() < 0 || requestParam.getLoginType() > 2) {
            throw new ClientException("参数有误");
        }

        // 判断是验证码还是密码
        int loginType = requestParam.getLoginType();
        UserDO userDO;
        if (loginType == UserConstant.USER_LOGIN_TYPE_PASSWORD) {
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
        doRateLimit(key, verifyCodeLimit, verifyCodeTtl, "验证码发送过于频繁，请稍后再试");

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
        if (requestParam.getType() == UserConstant.LOGIN_TYPE) {
            distributedCache.put(RedisKeyConstant.USER_LOGIN_VERIFY_CODE + email, verifyCode, verifyCodeTtl, TimeUnit.SECONDS);
        } else if (requestParam.getType() == UserConstant.REGISTER_TYPE) {
            distributedCache.put(RedisKeyConstant.USER_REGISTER_VERIFY_CODE + email, verifyCode, verifyCodeTtl, TimeUnit.SECONDS);
        } else {
            distributedCache.put(RedisKeyConstant.USER_RESET_VERIFY_CODE + email, verifyCode, verifyCodeTtl, TimeUnit.SECONDS);
        }


        return true;
    }

    /**
     * 用户注册
     * @param requestParam 用户注册参数
     */
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 责任链做校验
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(),requestParam);

        String email = requestParam.getEmail();
        String phone = requestParam.getPhone();

        // 对邮箱加锁，防止并发注册
        String lockKey = RedisKeyConstant.USER_REGISTER_LOCK + email;
        RLock registerLock = redissonClient.getLock(lockKey);
        boolean tryLock = registerLock.tryLock();
        if (!tryLock) {
            // 出现并发注册，返回错误信息
            throw new ClientException(UserRegisterErrorCodeEnum.USER_REGISTER_FAIL);
        }
        try {
            // 校验验证码是否正确
            String codeKey = RedisKeyConstant.USER_REGISTER_VERIFY_CODE + email;
            StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
            String code =  stringRedisTemplate.opsForValue().get(codeKey);
            if (code == null) {
                throw new ClientException(UserRegisterErrorCodeEnum.CODE_NOTNULL);
            }
            if (!code.equals(requestParam.getCode())) {
                throw new ClientException(UserRegisterErrorCodeEnum.CODE_ILLEGAL);
            }
            // 校验邮箱是否已经注册

            // 注册用户
            // 随机生成 6 位长度的盐
            String salt = RandomUtil.randomString(UserConstant.SALT_LENGTH);
            // 对密码进行加密
            String passwordWithMd5 = DigestUtil.md5Hex((requestParam.getPassword() + salt).getBytes());
            // 生成用户名
            String username = "user" + RandomUtil.randomNumbers(8);
            UserDO user = UserDO.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .salt(salt)
                    .password(passwordWithMd5)
                    .username(username)
                    .build();

            try {
                // 将用户信息插入数据库
                save(user);

                // 删除redis中验证码
                stringRedisTemplate.delete(codeKey);

                // 将用户邮箱添加到布隆过滤器中，防止缓存穿透
                userRegisterCachePenetrationBloomFilter.add(email);
            } catch (Exception e) {
                log.error("注册信息插入错误，注册信息：{}", requestParam);
                throw new ClientException(UserRegisterErrorCodeEnum.USER_REGISTER_FAIL);
            }
        } finally {
            registerLock.unlock();
        }
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     * @param email 邮箱
     * @return 是否存在
     */
    @Override
    public boolean hasEmail(String email){
        // 查询布隆过滤器是否存在
        return userRegisterCachePenetrationBloomFilter.contains(email);
    }

    /**
     * 限流操作
     *
     * @param key     限流key
     * @param limit   指定时间内允许的请求数
     * @param ttl     指定时间 单位秒
     * @param message 限流的提示信息
     */
    private void doRateLimit(String key, int limit, int ttl, String message) {
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

    /**
     * 用户信息修改
     * @param requestParam 用户修改信息参数
     */
    @Override
    public void userInfoUpdate(UserResetReqDTO requestParam) {
        // 校验参数
        if (requestParam == null || requestParam.getEmail() == null ||
                requestParam.getUsername() == null || requestParam.getOldPassword() == null || requestParam.getNewPassword() == null) {
            throw new ClientException("参数不能为空！");
        }

        // 1 获取用户信息判断是否登录
        // 1.1 ThreadLocal获取用户id
        String userId = UserContext.getUserId();
        // 1.2 校验用户id是否为空
        if (userId == null) {
            throw new ClientException("请先登录！");
        }
        // 1.3 检验用户id是否存在
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new ClientException("用户不存在！");
        }

        // 2 校验修改信息是否合法
        // 2.1 用户名长度
        if (requestParam.getUsername().length() > UserConstant.PASSWORD_MAX_LENGTH || requestParam.getUsername().length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new ClientException("用户名要在5~20位之间！");
        }

        // 2.2 旧密码是否正确
        // 加盐比对，规则是 原始密码 + salt 再bcrypt哈希
        String getPassword = DigestUtil.md5Hex((requestParam.getOldPassword() + user.getSalt()));
        if (!getPassword.equals(user.getPassword())) {
            throw new ClientException("密码错误！");
        }
        // 2.3 新密码是否合法
        if (requestParam.getNewPassword().length() > UserConstant.PASSWORD_MAX_LENGTH || requestParam.getNewPassword().length() < UserConstant.PASSWORD_MIN_LENGTH) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_ILLEGAL);
        }
        // 2.4 新密码和旧密码是否一致
        if (!requestParam.getOldPassword().equals(requestParam.getNewPassword())) {
            throw new ClientException("新旧密码不一致！");
        }
        if ((requestParam.getNewPassword() + user.getSalt()).equals(user.getPassword())) {
            throw new ClientException("新密码不能与旧密码相同！");

        }

        // 3 更新用户信息
        // 3.1 更新数据库
        distributedCache.delete(userId);
        UserDO updateUser = UserDO.builder()
                .email(requestParam.getEmail())
                .username(requestParam.getUsername())
                .password(getPassword)
                .build();
        updateUser.setId(Long.valueOf(userId));
        try {
            userMapper.updateById(updateUser);
        } catch (Exception e) {
            log.error("用户信息更新失败：{}", e.getMessage());
            throw new ClientException("个人信息更新失败！");
        }
        // TODO redis和mysql一致性保持(采用哪种处理方式)

    }
}
