package org.fuchuang.biz.passageservice.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.passageservice.common.constant.ParamConstant;
import org.fuchuang.biz.passageservice.dao.entity.mongo.CollectionDO;
import org.fuchuang.biz.passageservice.dao.entity.mongo.CommentDO;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.dao.mapper.mongo.CollectionRepository;
import org.fuchuang.biz.passageservice.dao.mapper.mongo.CommentRepository;
import org.fuchuang.biz.passageservice.dto.req.CommentReqDTO;
import org.fuchuang.biz.passageservice.dto.req.DoLikeReqDTO;
import org.fuchuang.biz.passageservice.remote.UserRemoteService;
import org.fuchuang.biz.passageservice.remote.dto.resp.UserPersonalInfoRespDTO;
import org.fuchuang.biz.passageservice.service.DbOpsService;
import org.fuchuang.biz.passageservice.service.PassageDoLikeService;
import org.fuchuang.biz.passageservice.service.idgenerate.IdGeneratorManager;
import org.fuchuang.framework.starter.bases.constant.RedisKeyConstant;
import org.fuchuang.framework.starter.cache.DistributedCache;
import org.fuchuang.framework.starter.convention.errorcode.BaseErrorCode;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户点赞，收藏，评论接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PassageDoLikeServiceImpl implements PassageDoLikeService {

    private final DistributedCache distributedCache;

    private final UserRemoteService userRemoteService;

    private final CommentRepository commentRepository;
    private final CollectionRepository collectionRepository;

    private final MongoTemplate mongoTemplate;

    private final DefaultRedisScript<Long> passageLikeScript;
    private final DefaultRedisScript<Long> passageUnLikeScript;
    private final DefaultRedisScript<Long> passageCollectScript;
    private final DefaultRedisScript<Long> passageUnCollectScript;

    private final DbOpsService dbOpsService;

    /**
     * 点赞
     * @param requestParam 点赞请求
     */
    @Override
    public void doLike(DoLikeReqDTO requestParam) {
        // 1.参数校验
        if(requestParam == null || StrUtil.isBlank(requestParam.getPassageId()) || StrUtil.isBlank(requestParam.getAuthorId())){
            throw new ClientException("参数有误");
        }
        // set集合的key，优化用bitmap存储
        String setKey = RedisKeyConstant.SET_LIKE_KEY + requestParam.getPassageId();

        // kv类型的key(key为passageId, value为点赞总数)
        String strKey = RedisKeyConstant.STRING_LIKE_KEY + requestParam.getPassageId();

        // 文章作者的点赞总数的key
        String userKey = RedisKeyConstant.USER_LIKES_SUM + requestParam.getAuthorId();

        // 获取当前用户信息
        String userId = UserContext.getUserId();
        // 获取当前用户点赞的文章集合key
        String nowUserKey = RedisKeyConstant.USER_SET_LIKE_KEY + userId;

        // 封装keys
        List<String> keys = Arrays.asList(setKey, strKey, userKey, nowUserKey);
        StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
        String passageId = requestParam.getPassageId();
        // 进行点赞操作
        Boolean isLiked = stringRedisTemplate.opsForValue().getBit(setKey, Long.parseLong(userId));
        if (requestParam.getType() == ParamConstant.DO_LIKE_OR_COLLECTION_TYPE) {
            // 查询用户是否点过赞
            if (Boolean.FALSE.equals(isLiked)) {
                // 添加到 redis
                // redis数据加一
                try {
                    // 执行lua脚本
                    stringRedisTemplate.execute(passageLikeScript, keys, userId, passageId);
                } catch (Exception e) {
                    if (e instanceof RedisSystemException){
                        log.error("重复点赞");
                        throw new ClientException("请勿重复点赞");
                    } else {
                        log.error("lua脚本执行失败: {}", e.toString());
                        throw new ServiceException("lua脚本执行失败");
                    }
                }
            } else {
                throw new ClientException("请勿重复点赞");
            }
        } // 取消点赞
        else {
            // 取消点赞
            // 判断是否点过赞
            if (Boolean.TRUE.equals(isLiked)) {
                // 如果点过赞
                // 删除数据
                // redis数据减一
                try {
                    // 执行lua脚本
                    stringRedisTemplate.execute(passageUnLikeScript, keys, userId, passageId);
                } catch (Exception e) {
                    if (e instanceof RedisSystemException) {
                        log.error("重复取消");
                        throw new ClientException("请勿重复取消");
                    } else {
                        log.error("lua脚本执行失败: {}", e.toString());
                        throw new ServiceException("lua脚本执行失败");
                    }
                }
            }
        }
    }

    /**
     * 收藏，和点赞逻辑类似
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doCollect(DoLikeReqDTO requestParam) {

        // 参数检验
        if(requestParam == null || StrUtil.isBlank(requestParam.getPassageId()) || StrUtil.isBlank(requestParam.getAuthorId())){
            throw new ClientException("参数有误");
        }

        //收藏文章的用户集合的key
        String setKey= RedisKeyConstant.SET_COLLECT_KEY + requestParam.getPassageId();

        //kv类型的key(key为passageId,value为收藏总数)
        String strKey=RedisKeyConstant.STRING_COLLECT_KEY + requestParam.getPassageId();

        //文章作者的收藏总数的key
        String userKey=RedisKeyConstant.USER_COLLECT_SUM + requestParam.getAuthorId();

        //获取userId
        String userIdStr = UserContext.getUserId();
        Long userId = Long.parseLong(userIdStr);
        //当前用户收藏的文章集合key
        String nowUserKey=RedisKeyConstant.USER_LIST_COLLECT_KEY + userIdStr;

        //收藏操作
        StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
        // 构造keys
        List<String> keys = Arrays.asList(setKey, strKey, userKey, nowUserKey);
        String passageIdStr = requestParam.getPassageId();
        long passageId = Long.parseLong(passageIdStr);
        if(requestParam.getType() == ParamConstant.DO_LIKE_OR_COLLECTION_TYPE){
            //添加到redis，以set方式存储，key为passageId，value为userId
            if(Boolean.FALSE.equals(stringRedisTemplate.opsForSet().isMember(setKey, userIdStr))){
                // 先添加到mongoDB
                // 生成全局唯一id
                String collectionId = IdGeneratorManager.generateId(userId);
                Date nowDate = new Date();
                // 构造收藏实体类
                CollectionDO collectionDO = CollectionDO.builder()
                        .id(collectionId)
                        .collectorId(userId)
                        .objectId(passageId)
                        .createTime(nowDate)
                        .updateTime(nowDate)
                        .delFlag(0)
                        .build();
                try {
                    collectionRepository.save(collectionDO);

                    //添加到redis
                    stringRedisTemplate.execute(passageCollectScript, keys, userIdStr, passageIdStr);
                }catch (Exception ex){
                    log.error("保存数据失败");
                    throw new ServiceException(BaseErrorCode.SERVICE_ERROR);
                }
            }
            else {
                throw new ClientException("重复收藏！");
            }
        }
        //取消收藏
        else {
            //判断是否收藏过
            if(Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(setKey, userIdStr))){
                //取消收藏
                // 逻辑删除mongo中的数据
                // 查询条件
                Criteria criteria = Criteria
                        .where("objectId").is(passageId)
                        .and("collectorId").is(userId);
                Query query = Query.query(criteria);
                // 修改条件
                Update delFlag = new Update().set("delFlag", 1);
                try {
                    mongoTemplate.updateFirst(query,delFlag,CollectionDO.class);

                    // 删除redis中
                    stringRedisTemplate.execute(passageUnCollectScript, keys, userIdStr, passageIdStr);
                }catch (Exception ex){
                    log.error("修改数据失败");
                    throw new ServiceException(BaseErrorCode.SERVICE_ERROR);
                }
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
    @Transactional(rollbackFor = Exception.class)
    public void doComment(CommentReqDTO requestParam) {
        // 参数校验
        if(requestParam == null || StrUtil.isBlank(requestParam.getPassageId()) || StrUtil.isBlank(requestParam.getContent())) {
            throw new ClientException("参数有误！");
        }

        // 得到userId
        String userIdstr = UserContext.getUserId();

        //得到当前用户信息
        Result<UserPersonalInfoRespDTO> result = userRemoteService.getUserInfo(userIdstr);
        if(result == null || !result.getCode().equals(Result.SUCCESS_CODE)) {
            throw new ClientException("获取用户信息失败！");
        }
        UserPersonalInfoRespDTO userInfo = result.getData();

        //得到评论，插入mongoDB
        Date nowDate = new Date();

        // 生成全局唯一ID
        long userId = Long.parseLong(userIdstr);
        String commentId = IdGeneratorManager.generateId(userId);
        // 构造评论实体
        CommentDO commentDO = CommentDO.builder()
                .id(commentId)
                .userId(userId)
                .username(userInfo.getUsername())
                .content(requestParam.getContent())
                .passageId(Long.valueOf(requestParam.getPassageId()))
                .parentId(StrUtil.isBlank(requestParam.getParentId()) ? 0L : Long.parseLong(requestParam.getParentId()))
                .createTime(nowDate)
                .updateTime(nowDate)
                .delFlag(0)
                .build();
        try {
            commentRepository.save(commentDO);

            // 插入成功后增加redis中评论的数量
            String key = RedisKeyConstant.STRING_COMMENT_KEY + requestParam.getPassageId();
            StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) distributedCache.getInstance();
            stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            throw new ClientException("评论插入有误！");
        }
    }
}
