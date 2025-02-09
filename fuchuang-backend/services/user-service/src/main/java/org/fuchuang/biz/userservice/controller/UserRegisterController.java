package org.fuchuang.biz.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.biz.userservice.service.UserRegisterService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegisterController {

    @Autowired
    private UserRegisterService userRegisterService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/api/user-service/v1/register")
    public Result<String> register(@RequestBody UserRegisterReqDTO requestParam) {
        return Results.success(userRegisterService.register(requestParam));
    }
}
