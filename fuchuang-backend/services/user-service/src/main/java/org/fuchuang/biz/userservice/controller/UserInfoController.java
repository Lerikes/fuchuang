package org.fuchuang.biz.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.userservice.dto.req.UserForgetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetPasswordReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserResetReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserPersonalInfoRespDTO;
import org.fuchuang.biz.userservice.service.UserInfoService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.fuchuang.frameworks.starter.user.core.UserInfoDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "用户信息控制层")
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/api/user-service/v1/user/{userId}")
    public Result<UserPersonalInfoRespDTO> getUserInfo(@PathVariable(required = false) Long userId) {
        UserPersonalInfoRespDTO result = userInfoService.getUserPersonalInfo(userId);
        return Results.success(result);
    }

    /**
     * 用户信息修改
     */
    @Operation(summary = "用户信息修改")
    @PostMapping("/api/user-service/v1/user")
    public Result<Void> updateUserInfo(@RequestBody UserResetReqDTO requestParam) {
        userInfoService.userInfoUpdate(requestParam);
        return Results.success();
    }

    /**
     * 用户密码重置接口
     * @param requestParam 重置密码请求参数
     * @return Result<Void>
     */
    @Operation(summary = "重置密码")
    @PostMapping("/api/user-service/v1/reset-password")
    public Result<Void> resetPassword(@RequestBody UserResetPasswordReqDTO requestParam) {
        userInfoService.resetPassword(requestParam);
        return Results.success();
    }

    /**
     * 用户忘记密码接口
     * @param requestParam 忘记密码请求参数
     * @return Result<Void>
     */
    @Operation(summary = "忘记密码")
    @PostMapping("/api/user-service/v1/forget-password")
    public Result<Void> forgetPassword(@RequestBody UserForgetPasswordReqDTO requestParam) {
        userInfoService.forgetPassword(requestParam);
        return Results.success();
    }

    /**
     * 用户头像上传
     */
    @Operation(summary = "头像上传")
    @PostMapping("/api/user-service/v1/image/upload")
    public Result<Void> uploadUserImage(@RequestBody MultipartFile imageFile) {
        userInfoService.uploadUserImage(imageFile);
        return Results.success();
    }
}
