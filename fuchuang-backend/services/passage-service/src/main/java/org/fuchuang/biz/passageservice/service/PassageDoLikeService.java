package org.fuchuang.biz.passageservice.service;

import org.fuchuang.framework.starter.convention.result.Result;

/**
 * 用户点赞，收藏，评论接口
 */
public interface PassageDoLikeService {

    /**
     * 点赞
     * @param passageId 文章id
     * @param userId 上传者id
     * @param type 1为点赞，0为取消点赞
     */
    void doLike(String passageId, String userId, int type);

    /**
     * 收藏
     * @param passageId 视频id
     * @param authorId 作者id
     * @param type 1为收藏，0为取消收藏
     */
    void doCollect(String passageId, String authorId, int type);

    /**
     * 评论
     * @param passageId 视频id
     * @param parentId 父评论id
     * @param content 评论内容
     */
    void doComment(String passageId, String parentId, String content);
}
