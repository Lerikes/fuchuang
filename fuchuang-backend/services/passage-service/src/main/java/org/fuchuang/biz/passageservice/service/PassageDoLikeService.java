package org.fuchuang.biz.passageservice.service;

import org.fuchuang.biz.passageservice.dto.req.CommentReqDTO;
import org.fuchuang.biz.passageservice.dto.req.DoLikeReqDTO;

/**
 * 用户点赞，收藏，评论接口
 */
public interface PassageDoLikeService {

    /**
     * 点赞
     */
    void doLike(DoLikeReqDTO doLikeReqDTO);

    /**
     * 收藏
     */
    void doCollect(DoLikeReqDTO doLikeReqDTO);

    /**
     * 评论
     */
    void doComment(CommentReqDTO commentReqDTO);
}
