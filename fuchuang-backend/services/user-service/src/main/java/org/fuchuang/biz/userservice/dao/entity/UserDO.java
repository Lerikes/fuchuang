package org.fuchuang.biz.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.fuchuang.framework.starter.database.base.BaseDO;

/**
 * 用户实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_user")
public class UserDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 加盐
     */
    @TableField("salt")
    private String salt;

    /**
     * 头像
     */
    @TableField("image")
    private String image;

    /**
     * 签名
     */
    @TableField("signature")
    private String signature;
}
