package org.fuchuang.biz.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.userservice.dto.req.UserLoginReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.biz.userservice.dto.req.UserSendCodeReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;
import org.fuchuang.biz.userservice.service.UserLoginService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "用户登录控制层")
public class UserLoginController {

    private final UserLoginService userLoginService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/api/user-service/v1/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userLoginService.login(requestParam));
    }

    /**
     * 发送验证码
     * @param requestParam 发送验证码请求参数
     * @return Result<Void>
     */
    @Operation(summary = "发送验证码")
    @PostMapping("/api/user-service/v1/send-verify-code")
    public Result<Boolean> sendVerifyCode(@RequestBody UserSendCodeReqDTO requestParam) {
        return Results.success(userLoginService.sendVerifyCode(requestParam));
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/api/user-service/v1/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userLoginService.register(requestParam);
        return Results.success();
    }

    /**
     * 通过 Token 检查用户是否登录
     */
    @Operation(summary = "通过 Token 检查用户是否登录")
    @GetMapping("/api/user-service/check-login")
    public Result<UserLoginRespDTO> checkLogin(@RequestParam("accessToken") String accessToken) {
        UserLoginRespDTO result = userLoginService.checkLogin(accessToken);
        return Results.success(result);
    }

    /**
     * 用户退出登录
     */
    @Operation(summary = "用户退出登录")
    @GetMapping("/api/user-service/logout")
    public Result<Void> logout(@RequestParam(required = false) String accessToken) {
        userLoginService.logout(accessToken);
        return Results.success();
    }
}
