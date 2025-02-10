package org.fuchuang.biz.userservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息修改请求参数")
public class UserResetReqDTO {

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

    /**
     * 邮箱
     */
    @Email
    @Schema(description = "邮箱")
    private String email;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
}
