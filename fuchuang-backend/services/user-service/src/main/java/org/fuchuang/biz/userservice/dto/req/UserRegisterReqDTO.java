package org.fuchuang.biz.userservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户注册请求参数")
public class UserRegisterReqDTO {

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 邮箱
     */
    @Email
    @Schema(description = "邮箱")
    private String email;

    /**
     * 电话号(可要可不要)
     */
    @Schema(description = "电话号")
    private String phone;

    /**
     * 注册验证码
     */
    @Schema(description = "注册验证码")
    private String code;
}
