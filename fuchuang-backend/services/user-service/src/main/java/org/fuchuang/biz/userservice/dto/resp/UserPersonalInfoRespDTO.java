package org.fuchuang.biz.userservice.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取用户信息返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "获取用户信息返回参数")
public class UserPersonalInfoRespDTO {

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
     * 头像
     */
    @Schema(description = "头像")
    private String image;

    /**
     * 签名
     */
    @Schema(description = "签名")
    private String signature;
}
