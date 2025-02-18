package org.fuchuang.biz.passageservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.fuchuang.framework.starter.database.base.BaseDO;

import java.math.BigDecimal;

/**
 * 文章实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_passage")
public class PassageDO extends BaseDO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章分区id
     */
    @TableField("partition")
    private String partition;

    /**
     * 文章作者用户id
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 文章作者用户
     */
    @TableField("user_name")
    private String userName;

    /**
     * 文章点赞数
     */
    @TableField("likes")
    private Long likes;

    /**
     * 文章收藏数
     */
    @TableField("collection")
    private Long collection;

    /**
     * 文章观看次数
     */
    @TableField("views")
    private Long views;

    /**
     * 文章图片地址(可能有多个图片，在数据库中用逗号隔开)
     */
    @TableField("images")
    private String images;

    /**
     * 是否进行过虚假新闻校验
     */
    @TableField("is_check")
    private Boolean isCheck;

    /**
     * 文章虚假率
     */
    @TableField("fake_rate")
    private BigDecimal fakeRate;

    /**
     * 文章状态 0 草稿 1 已发布
     */
    @TableField("status")
    private Integer status;
}
