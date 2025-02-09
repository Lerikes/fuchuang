package org.fuchuang.biz.userservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class RedisKeyConstant {

    /**
     * 用户注册可复用用户名分片，Key Prefix + Idx
     */
    public static final String USER_REGISTER_REUSE_SHARDING = "fuchuang-user-service:user-reuse:";

    /**
     * 用户登录验证码，Key Prefix + email
     */
    public static final String USER_LOGIN_VERIFY_CODE = "fuchuang-user-service:user-verifyCode:";
}
