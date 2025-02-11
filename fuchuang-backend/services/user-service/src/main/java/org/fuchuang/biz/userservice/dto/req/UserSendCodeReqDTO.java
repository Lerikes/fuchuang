package org.fuchuang.biz.userservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录发送验证码请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录发送验证码请求参数")
public class UserSendCodeReqDTO {
    /**
     * 申请验证码类型，1：登录 2：注册 0：重置 3: 忘记密码
     */
    @Schema(description = "申请类型")
    private int type;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
}
