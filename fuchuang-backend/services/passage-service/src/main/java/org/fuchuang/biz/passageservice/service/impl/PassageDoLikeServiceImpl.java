package org.fuchuang.biz.passageservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.passageservice.common.constant.ParamConstant;
import org.fuchuang.biz.passageservice.dao.entity.CommentDO;
import org.fuchuang.biz.passageservice.dao.mapper.CommentMapper;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.dto.req.CommentReqDTO;
import org.fuchuang.biz.passageservice.dto.req.DoLikeReqDTO;
import org.fuchuang.biz.passageservice.service.PassageDoLikeService;
import org.fuchuang.framework.starter.bases.constant.RedisKeyConstant;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 用户点赞，收藏，评论接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PassageDoLikeServiceImpl implements PassageDoLikeService {

    private final StringRedisTemplate stringRedisTemplate;

    private final PassageMapper passageMapper;

    private final CommentMapper commentMapper;

    /**
     * 点赞
     * @param doLikeReqDTO 点赞请求
     */
    @Override
    public void doLike(DoLikeReqDTO doLikeReqDTO) {
        // set集合的key
        String setKey = RedisKeyConstant.SET_LIKE_KEY + doLikeReqDTO.getPassageId();

        // kv类型的key(key为passageId, value为点赞总数)
        String strKey = RedisKeyConstant.STRING_LIKE_KEY + doLikeReqDTO.getPassageId();

        // 文章作者的点赞总数的key
        String userKey = RedisKeyConstant.USER_LIKES_SUM + doLikeReqDTO.getAuthorId();

        // 获取当前用户信息
        String userId = UserContext.getUserId();
        // 获取当前用户点赞的文章集合key
        String nowUserKey = RedisKeyConstant.USER_SET_LIKE_KEY + userId;

        // 进行点赞操作
        if (doLikeReqDTO.getType() == ParamConstant.DO_LIKE_OR_COLLECTION_TYPE) {

            // 添加到redis，set存储，key为passageId，value为userId
            if(Boolean.FALSE.equals(this.stringRedisTemplate.opsForSet().isMember(setKey, String.valueOf(userId)))){

                //添加到redis
                this.stringRedisTemplate.opsForSet().add(setKey, String.valueOf(userId));
                this.stringRedisTemplate.opsForSet().add(nowUserKey,String.valueOf(doLikeReqDTO.getPassageId()));
                // TODO redis相关数据 +1

            }
            else {
                throw new ClientException("重复点赞！");
            }
        } // 取消点赞
        else {
            // 判断是否点过赞
            if(Boolean.TRUE.equals(this.stringRedisTemplate.opsForSet().isMember(setKey, String.valueOf(userId)))){
                //取消点赞
                this.stringRedisTemplate.opsForSet().remove(setKey, userId);
                this.stringRedisTemplate.opsForSet().remove(nowUserKey,String.valueOf(doLikeReqDTO.getPassageId()));
                // TODO redis相关数据 -1

            }
            else {
                throw new ClientException("重复取消！");
            }
        }
    }

    /**
     * 收藏，和点赞逻辑类似
     */
    @Override
    public void doCollect(DoLikeReqDTO doLikeReqDTO) {
        //set集合的key
        String setKey= RedisKeyConstant.SET_COLLECT_KEY + doLikeReqDTO.getPassageId();

        //kv类型的key(key为passageId,value为点赞总数)
        String strKey=RedisKeyConstant.STRING_COLLECT_KEY + doLikeReqDTO.getPassageId();

        //视频作者的点赞总数的key
        String userKey=RedisKeyConstant.USER_COLLECT_SUM + doLikeReqDTO.getAuthorId();

        //获取userId
        String userId = UserContext.getUserId();
        //当前用户收藏的视频集合key
        String nowUserKey=RedisKeyConstant.USER_LIST_COLLECT_KEY + userId;

        //收藏操作
        if(doLikeReqDTO.getType() == ParamConstant.DO_LIKE_OR_COLLECTION_TYPE){
            //添加到redis，以set方式存储，key为passageId，value为userId
            if(Boolean.FALSE.equals(this.stringRedisTemplate.opsForSet().isMember(setKey, userId))){
                //添加到redis
                this.stringRedisTemplate.opsForSet().add(setKey, userId);
                this.stringRedisTemplate.opsForList().leftPush(nowUserKey,String.valueOf(doLikeReqDTO.getPassageId()));
                // TODO redis相关数据 +1

            }
            else {
                throw new ClientException("重复收藏！");
            }
        }
        //取消收藏
        else {
            //判断是否点过赞
            if(Boolean.TRUE.equals(this.stringRedisTemplate.opsForSet().isMember(setKey, userId))){
                //取消点赞
                this.stringRedisTemplate.opsForSet().remove(setKey, userId);
                this.stringRedisTemplate.opsForList().remove(nowUserKey,1,String.valueOf(doLikeReqDTO.getPassageId()));
                // TODO redis相关数据 -1

            }
            else {
                throw new ClientException("重复取消！");
            }
        }
    }

    /**
     * 评论
     */
    @Override
    public void doComment(CommentReqDTO commentReqDTO) {
        // 得到userId
        String userId = UserContext.getUserId();

        // 视频的评论总数的key
        String key = RedisKeyConstant.STRING_COMMENT_KEY + commentReqDTO.getPassageId();

        // TODO redis数据 +1

        //得到当前用户信息

        //得到评论，插入数据库
        CommentDO comment = CommentDO.builder()
                .userId(Long.valueOf(userId))
                .content(commentReqDTO.getContent())
                .passageId(Long.valueOf(commentReqDTO.getPassageId()))
                .parentId(commentReqDTO.getParentId() == null ? 0L : Long.parseLong(commentReqDTO.getParentId()))
                .build();
        try {
            commentMapper.insert(comment);
        } catch (Exception e) {
            throw new ClientException("评论插入有误！");
        }
    }
}
