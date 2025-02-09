package org.fuchuang.biz.userservice.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.userservice.common.constant.RedisKeyConstant;
import org.fuchuang.biz.userservice.common.constant.UserConstant;
import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dao.mapper.UserMapper;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.biz.userservice.service.UserRegisterService;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 用户注册接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserRegisterService {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String register(UserRegisterReqDTO userRegisterReqDTO) {
        // 校验参数
        if (userRegisterReqDTO == null || userRegisterReqDTO.getEmail() == null ||
                userRegisterReqDTO.getPassword() == null || userRegisterReqDTO.getUsername() == null) {
            throw new ClientException(UserRegisterErrorCodeEnum.USER_REGISTER_FAIL);
        }

        // 获取用户传入注册信息
        String email = userRegisterReqDTO.getEmail();
        String password = userRegisterReqDTO.getPassword();
        String username = userRegisterReqDTO.getUsername();
        String phone = userRegisterReqDTO.getPhone();

        // 校验密码是否合法
        if (password.length() < UserConstant.PASSWORD_MIN_LENGTH || password.length() > UserConstant.PASSWORD_MAX_LENGTH) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_ILLEGAL);
        }
        // 检验用户名是否存在
        if (this.existsAccountByUsername(username)) {
            throw new ClientException(UserRegisterErrorCodeEnum.HAS_USERNAME_NOTNULL);
        }

        // 对邮箱加锁，防止并发注册
        RLock registerLock = redissonClient.getLock(RedisKeyConstant.USER_REGISTER_LOCK + email);
        boolean tryLock = registerLock.tryLock();
        if (!tryLock) {
            // 出现并发注册，返回错误信息
            throw new ClientException(UserRegisterErrorCodeEnum.USER_REGISTER_FAIL);
        }
        try {
            // 校验验证码是否正确
            String code = this.getEmailVerifyCode(email);
            if (code == null) {
                throw new ClientException(UserRegisterErrorCodeEnum.CODE_NOTNULL);
            }
            if (!code.equals(userRegisterReqDTO.getCode())) {
                throw new ClientException(UserRegisterErrorCodeEnum.CODE_ILLEGAL);
            }
            // 校验邮箱是否已经注册
            if (this.existsAccountByEmail(email)) {
                throw new ClientException(UserRegisterErrorCodeEnum.MAIL_REGISTERED);
            }
            // 注册用户
            // 随机生成 6 位长度的盐
            String salt = RandomUtil.randomString(UserConstant.SALT_LENGTH);
            // 对密码进行加密
            String passwordWithMd5 = DigestUtil.md5Hex((password + salt).getBytes());
            UserDO user = UserDO.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .salt(salt)
                    .password(passwordWithMd5)
                    .username(username)
                    .build();
            // 将用户信息插入数据库
            try {
                save(user);
            } catch (Exception e) {
                log.error("注册信息插入错误，注册信息：{}", userRegisterReqDTO);
                throw new ClientException(UserRegisterErrorCodeEnum.USER_REGISTER_FAIL);
            }
            // 返回注册成功
            return "注册成功!";
        } finally {
            registerLock.unlock();
        }
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<UserDO>query().eq("email", email));
    }

    /**
     * 查询指定用户名的用户是否已经存在
     * @param username 用户名
     * @return 是否存在
     */
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<UserDO>query().eq("username", username));
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = RedisKeyConstant.USER_REGISTER_VERIFY_CODE + email;
        return stringRedisTemplate.opsForValue().get(key);
    }
}
