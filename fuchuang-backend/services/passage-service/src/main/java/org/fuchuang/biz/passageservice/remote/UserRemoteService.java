package org.fuchuang.biz.passageservice.remote;

import io.swagger.v3.oas.annotations.Operation;
import org.fuchuang.biz.userservice.dto.resp.UserPersonalInfoRespDTO;
import org.fuchuang.framework.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务远程调用
 */
@FeignClient("fuchuang-user-service")
public interface UserRemoteService {

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/api/user-service/v1/user")
    Result<UserPersonalInfoRespDTO> getUserInfo(@RequestParam(value = "userId") String userId);
}
