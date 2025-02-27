package org.fuchuang.biz.userservice.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录返回参数")
public class UserLoginRespDTO {

    /**
     * 用户 ID
     */
    @Schema(description = "userId")
    private String userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * Token
     */
    @Schema(description = "Token")
    private String accessToken;
}
