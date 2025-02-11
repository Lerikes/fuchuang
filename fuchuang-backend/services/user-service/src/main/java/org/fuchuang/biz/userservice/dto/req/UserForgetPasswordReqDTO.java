package org.fuchuang.biz.userservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户忘记密码请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户忘记密码请求参数")
public class UserForgetPasswordReqDTO {

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;

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
