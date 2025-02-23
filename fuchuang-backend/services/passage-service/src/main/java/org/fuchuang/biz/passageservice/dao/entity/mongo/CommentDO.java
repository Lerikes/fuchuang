package org.fuchuang.biz.passageservice.dao.entity.mongo;

import lombok.*;
import org.fuchuang.framework.starter.database.base.BaseDO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * mongo中文章评论
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("comment")
public class CommentDO {

    /**
     * 评论id
     */
    @Id
    private Long id;

    /**
     * 对应文章id
     */
    private Long passageId;

    /**
     * 评论发布用户id
     */
    private Long userId;

    /**
     * 评论发布用户名称
     */
    private String username;

    /**
     * 评论主体
     */
    private String content;

    /**
     * 支持多级评论
     */
    private Long parentId;

    /**
     * 评论时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    private Integer delFlag;
}
