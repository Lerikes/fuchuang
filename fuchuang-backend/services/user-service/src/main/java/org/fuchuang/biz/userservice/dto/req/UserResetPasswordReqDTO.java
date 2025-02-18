package org.fuchuang.biz.userservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户密码修改参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户密码修改参数")
public class UserResetPasswordReqDTO {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 旧密码
     */
    @Schema(description = "旧密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码")
    private String newPassword;
}
