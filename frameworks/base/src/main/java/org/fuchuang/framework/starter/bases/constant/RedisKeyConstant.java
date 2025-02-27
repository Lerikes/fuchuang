package org.fuchuang.framework.starter.bases.constant;

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
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_KEY = "fuchuang:user-service:user:";

    /**
     * 用户注册锁的redis前缀
     */
    public static final String USER_REGISTER_LOCK = "fuchuang-user-service:register:lock";

    /**
     * 以passageId为key的集合，记录点赞的user集合
     */
    public static final String SET_LIKE_KEY = "fuchuang-passage-service:passage_set_like:";

    /**
     * 以passageId为key的集合，记录收藏的user集合
     */
    public static final String SET_COLLECT_KEY = "fuchuang-passage-service:passage_set_collect:";

    /**
     * 点赞数
     */
    public static final String STRING_LIKE_KEY = "fuchuang-passage-service:string_like:";

    /**
     * 收藏数
     */
    public static final String STRING_COLLECT_KEY = "fuchuang-passage-service:string_collect:";

    /**
     * 评论数
     */
    public static final String STRING_COMMENT_KEY = "fuchuang-passage-service:string_comment:";

    /**
     * 记录被总点赞数的key
     */
    public static final String USER_LIKES_SUM = "fuchuang-passage-service:passage_likes_sum:";

    /**
     * 以userId为key的list，记录点赞过的文章id
     */
    public static final String USER_SET_LIKE_KEY = "fuchuang-passage-service:user_set_like:";

    /**
     * 记录user被总收藏数的key
     */
    public static final String USER_COLLECT_SUM = "fuchuang-passage-service:video_collect_sum:";

    /**
     * 以userId为key的list，记录收藏过的文章id
     */
    public static final String USER_LIST_COLLECT_KEY="fuchuang-passage-service:use_set_collect:";

    /**
     * 用户简略信息的redis过期时间
     */
    public static final Integer REDIS_USER_INFO_TTL = 24 * 60 * 60;
}
