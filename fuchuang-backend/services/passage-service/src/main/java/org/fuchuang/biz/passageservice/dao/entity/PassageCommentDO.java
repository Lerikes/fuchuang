package org.fuchuang.biz.passageservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.fuchuang.framework.starter.database.base.BaseDO;

/**
 * 文章评论
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_passage_comment")
public class PassageCommentDO extends BaseDO {

    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对应文章id
     */
    @TableField("passage_id")
    private Long passageId;

    /**
     * 评论发布用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 评论主体
     */
    @TableField("content")
    private String content;

    /**
     * 支持多级评论
     */
    @TableField("parent_id")
    private Long parentId;
}
