package org.fuchuang.biz.userservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class RedisKeyConstant {

    /**
     * 用户登录验证码，Key Prefix + email
     */
    public static final String USER_LOGIN_VERIFY_CODE = "fuchuang-user-service:login:user-verifyCode:";

    /**
     * 用户注册验证码，Key Prefix + email
     */
    public static final String USER_REGISTER_VERIFY_CODE = "fuchuang-user-service:register:user-verifyCode:";

    /**
     * 用户重置验证码，Key Prefix + email
     */
    public static final String USER_RESET_VERIFY_CODE = "fuchuang-user-service:reset:user-verifyCode:";

    /**
     * 用户忘记密码验证码，Key Prefix + email
     */
    public static final String USER_FORGET_PASSWORD_VERIFY_CODE = "fuchuang-user-service:forgetPassword:user-verifyCode:";

    /**
     * 用户发送验证码限流，Key Prefix + email
     */
    public static final String USER_SEND_CODE_LIMIT = "fuchuang-user-service:limit:sendCode:";

    /**
     * 用户注册锁的redis前缀
     */
    public static final String USER_REGISTER_LOCK = "fuchuang-user-service:register:lock";

    /**
     * 用户信息锁
     */
    public static final String USER_INFO_LOCK = "fuchuang:user-service:user:";
}
