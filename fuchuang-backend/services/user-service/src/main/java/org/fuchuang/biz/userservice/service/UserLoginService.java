package org.fuchuang.biz.userservice.service;

import org.fuchuang.biz.userservice.dto.req.*;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;

/**
 * 用户登录相关接口
 */
public interface UserLoginService {

    /**
     * 用户登录
     * @param requestParam 用户登录参数
     * @return 用户基本信息
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 发送验证码
     * @param requestParam 发送验证码请求参数
     */
    boolean sendVerifyCode(UserSendCodeReqDTO requestParam);

    /**
     * 用户注册
     * @param requestParam 用户注册参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 通过 Token 检查用户是否登录
     * @param accessToken 用户登录 Token 凭证
     * @return 用户是否登录返回结果
     */
    UserLoginRespDTO checkLogin(String accessToken);

    /**
     * 检查email是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean hasEmail(String email);

    /**
     * 用户退出登录
     * @param accessToken 用户登录 Token 凭证
     */
    void logout(String accessToken);

    /**
     * 用户信息修改
     * @param requestParam 用户修改信息参数
     */
    void userInfoUpdate(UserResetReqDTO requestParam);

    /**
     * 用户忘记密码
     * @param requestParam 忘记密码请求参数
     */
    void forgetPassword(UserForgetPasswordReqDTO requestParam);
}
