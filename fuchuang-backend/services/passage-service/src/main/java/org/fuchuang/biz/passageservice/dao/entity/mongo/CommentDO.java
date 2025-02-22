package org.fuchuang.biz.passageservice.dao.entity.mongo;

import lombok.*;
import org.fuchuang.framework.starter.database.base.BaseDO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongo中文章评论
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("comment")
public class CommentDO extends BaseDO {

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
}
