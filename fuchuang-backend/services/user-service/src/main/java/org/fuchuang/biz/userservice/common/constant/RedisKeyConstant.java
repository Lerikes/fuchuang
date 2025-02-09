package org.fuchuang.biz.userservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class RedisKeyConstant {

    /**
     * 用户登录验证码，Key Prefix + email
     */
    public static final String USER_LOGIN_VERIFY_CODE = "fuchuang-user-service:user-verifyCode:";

    /**
     * 用户发送验证码限流，Key Prefix + email
     */
    public static final String USER_SEND_CODE_LIMIT = "fuchuang-user-service:limit:sendCode:";
}
