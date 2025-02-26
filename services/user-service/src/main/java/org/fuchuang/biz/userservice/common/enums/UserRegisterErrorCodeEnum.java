package org.fuchuang.biz.userservice.common.enums;

import org.fuchuang.framework.starter.convention.errorcode.IErrorCode;
import lombok.AllArgsConstructor;

/**
 * 用户注册错误码枚举
 */
@AllArgsConstructor
public enum UserRegisterErrorCodeEnum implements IErrorCode {

    USER_REGISTER_FAIL("A006000", "用户注册失败"),

    USER_NAME_NOTNULL("A006001", "用户名不能为空"),

    PASSWORD_NOTNULL("A006002", "密码不能为空"),

    PHONE_NOTNULL("A006003", "手机号不能为空"),

    ID_TYPE_NOTNULL("A006004", "证件类型不能为空"),

    ID_CARD_NOTNULL("A006005", "证件号不能为空"),

    HAS_USERNAME_NOTNULL("A006006", "用户名已存在"),

    PHONE_REGISTERED("A006007", "手机号已被占用"),

    MAIL_REGISTERED("A006008", "邮箱已被占用"),

    MAIL_NOTNULL("A006009", "邮箱不能为空"),

    USER_TYPE_NOTNULL("A006010", "旅客类型不能为空"),

    POST_CODE_NOTNULL("A006011", "邮编不能为空"),

    ADDRESS_NOTNULL("A006012", "地址不能为空"),

    REGION_NOTNULL("A006013", "国家/地区不能为空"),

    TELEPHONE_NOTNULL("A006014", "固定电话不能为空"),

    CODE_ILLEGAL("A006015", "验证码有误"),

    CODE_NOTNULL("A006016", "验证码不能为空"),

    EMAIL_ILLEGAL("A006016", "邮箱号不合法"),

    PASSWORD_ILLEGAL("A006017", "密码长度必须在5~20位之间"),

    PASSWORD_NOT_MATCH("A006018", "两次输入密码不一致");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示消息
     */
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
