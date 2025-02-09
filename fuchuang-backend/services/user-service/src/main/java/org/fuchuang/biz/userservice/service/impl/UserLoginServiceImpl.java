package org.fuchuang.biz.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.userservice.common.constant.RedisKeyConstant;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dao.mapper.UserMapper;
import org.fuchuang.biz.userservice.dto.req.UserLoginReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;
import org.fuchuang.biz.userservice.service.UserLoginService;
import org.fuchuang.framework.starter.cache.DistributedCache;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.frameworks.starter.user.core.UserInfoDTO;
import org.fuchuang.frameworks.starter.user.toolkit.JWTUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    
    /**
     * 用户登录
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
        if(loginType == 0){
            // 密码登录
            // 参数校验
            if (StrUtil.isBlank(requestParam.getPassword())) {
                throw new ClientException("密码不能为空");
            }
            // 根据邮箱查询用户信息
            userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                    .eq(UserDO::getEmail, requestParam.getEmail()));
            if (userDO == null) {
                throw  new ClientException("用户不存在");
            }
            // 加盐比对，规则是 原始密码 + salt 再bcrypt哈希
            String getPassword = DigestUtil.md5Hex((requestParam.getPassword() + userDO.getSalt()));
            if(!getPassword.equals(userDO.getPassword())){
                throw new ClientException("密码错误");
            }
        }else {
            // 验证码登录
            // 参数校验
            if (StrUtil.isBlank(requestParam.getCode())){
                throw new ClientException("验证码不能为空");
            }
            // 根据邮箱查询用户信息
            userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                    .eq(UserDO::getEmail, requestParam.getEmail()));
            if (userDO == null) {
                throw  new ClientException("用户不存在");
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
