package org.fuchuang.biz.passageservice.dao.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * mongoDB中文章点赞信息
 */
@Data
@Document("passage_like")
public class PassageLikeDO {
    /**
     * 主键
     */
    private String id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文章id
     */
    private Long passageId;

    /**
     * 是否点赞
     * 1 是
     * 0 否
     */
    private Integer isLike;

    /**
     * 是否收藏
     * 1 是
     * 0 否
     */
    private Integer isCollect;

    /**
     * 评论
     */
    private List<String> commentList;
}
