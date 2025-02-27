package org.fuchuang.biz.userservice.service;

import org.fuchuang.biz.userservice.dto.req.UserForgetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserPersonalInfoRespDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息相关接口
 */
public interface UserInfoService {

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户个人信息
     */
    UserPersonalInfoRespDTO getUserPersonalInfo(String userId);

    /**
     * 用户信息修改
     * @param requestParam 用户修改信息参数
     */
    void userInfoUpdate(UserResetReqDTO requestParam);

    /**
     * 用户密码重置
     * @param requestParam 用户密码重置请求参数
     */
    void resetPassword(UserResetPasswordReqDTO requestParam);

    /**
     * 用户忘记密码
     * @param requestParam 忘记密码请求参数
     */
    void forgetPassword(UserForgetPasswordReqDTO requestParam);

    /**
     * 用户头像上传
     * @param imageFile 用户头像文件
     */
    void uploadUserImage(MultipartFile imageFile);
}
