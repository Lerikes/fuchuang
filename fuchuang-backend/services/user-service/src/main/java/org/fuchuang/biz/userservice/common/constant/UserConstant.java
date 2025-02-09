package org.fuchuang.biz.userservice.common.constant;

// 用户信息常量
public class UserConstant {
    /**
     *盐值大小
     */
    public static final int SALT_LENGTH = 6;

    /**
     *登录状态码
     */
    public static final int LOGIN_TYPE = 1;

    /**
     *注册状态码
     */
    public static final int REGISTER_TYPE = 2;

    /**
     *重置状态码
     */
    public static final int RESET_TYPE = 0;

    /**
     * 用户名最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 用户名最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 使用密码登录
     */
    public static final Integer USER_LOGIN_TYPE_PASSWORD = 0;

    /**
     * 使用验证码登录
     */
    public static final Integer USER_LOGIN_TYPE_CODE = 1;
}
