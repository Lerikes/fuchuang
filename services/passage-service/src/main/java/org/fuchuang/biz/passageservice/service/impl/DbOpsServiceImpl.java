package org.fuchuang.biz.passageservice.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;
import org.fuchuang.biz.passageservice.dao.entity.mongo.PassageLikeDO;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.service.DbOpsService;
import org.fuchuang.framework.starter.bases.constant.RedisKeyConstant;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数据库操作的实现类，包括 redis 和 mongoDB
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbOpsServiceImpl implements DbOpsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MongoTemplate mongoTemplate;
    private final PassageMapper passageMapper;

    /**
     * 增加值的 Lua 脚本
     */
    private static final DefaultRedisScript<Long> ADD_INT_SAFELY_SCRIPT;

    /**
     * 刷新数据到mysql的线程池
     */
    private static final ExecutorService REFRESH_POOL = new ThreadPoolExecutor(
            3,// like collect comment
            6,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static {
        ADD_INT_SAFELY_SCRIPT = new DefaultRedisScript<>();
        // 设置脚本位置
        ADD_INT_SAFELY_SCRIPT.setLocation(new ClassPathResource("/lua/addIntSafely.lua"));
        // 设置返回类型
        ADD_INT_SAFELY_SCRIPT.setResultType(Long.class);
    }

    /**
     * 安全的向 redis 的 set 中添加值(使用 lua 脚本)
     *
     * @param key key
     * @param num 需要增加的值
     */
    @Override
    public void addIntSafely(String key, int num) {
        log.info("增加: {}", key);
        try {
            stringRedisTemplate.execute(ADD_INT_SAFELY_SCRIPT, Collections.singletonList(key), String.valueOf(num));
        } catch (Exception e) {
            log.error("lua脚本执行失败: {}", e.toString());
            throw new ServiceException("lua脚本执行失败");
        }
    }

    /**
     * 将数据异步插入 mongoDB 中
     *
     * @param userId  用户id
     * @param passageId 文章id
     * @param type    操作类型(1 点赞 2收藏 3评论)
     * @param ops     添加到对应字段的参数
     */
    @Override
    @Async(value = "mongoThreadPoolExecutor") // 设置自定义的线程池
    public void insertIntoMongoDB(String userId, String passageId, int type, Object ops) {
        // 1.查询当用户id和视频id所在的字段
        // 1.1封装查询条件
        Criteria criteria = Criteria
                .where("userId").is(userId)
                .and("passageId").is(passageId);
        Query query = Query.query(criteria);
        // 1.2查找点赞实体
        PassageLikeDO passageLikeDO = mongoTemplate.findOne(query, PassageLikeDO.class);

        // 2.检查是否存在
        if (passageLikeDO == null) {
            // 不存在，添加 userId 和 passageId
            passageLikeDO = new PassageLikeDO();
            passageLikeDO.setUserId(Long.parseLong(userId));
            passageLikeDO.setPassageId(Long.parseLong(passageId));
        }

        // 3.根据类型对相应的字段进行更新操作
        switch (type) {
            //点赞
            case 1:
                passageLikeDO.setIsLike((Integer) ops);
                break;
            //收藏
            case 2:
                passageLikeDO.setIsCollect((Integer) ops);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        mongoTemplate.save(passageLikeDO);
    }

    /**
     * 要是发现redis中like字段过期，则从数据库中查询数据返回，并同时把此视频所有字段刷新到redis
     * @param passageId 视频id
     * @return 点赞数
     */
    @Override
    public Long getSumFromDB(String passageId) {
        // 从数据库中查询当前文章的数据
        PassageDO passageDO = passageMapper.selectById(Long.parseLong(passageId));
        // 刷新到redis，同时刷新likes,collects,comments字段
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.STRING_LIKE_KEY + passageId,passageDO.getLikes().toString());
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.STRING_COLLECT_KEY + passageId,passageDO.getCollection().toString());
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.STRING_COMMENT_KEY + passageId,passageDO.getCommentCounts().toString());
        return passageDO.getLikes();
    }

    /**
     * 把 redis 中 kv 类似的数据定时刷新到 mysql 中
     * 每 12 小时执行一次
     */
    @PostConstruct
    @Scheduled(cron = "0 0 */12 * * *")
    public void refresh() {
        Set<String> likeKeys = scanKeys(RedisKeyConstant.STRING_LIKE_KEY + '*');
        Set<String> collectKeys = scanKeys(RedisKeyConstant.STRING_COLLECT_KEY + '*');
        Set<String> commentKeys = scanKeys(RedisKeyConstant.STRING_COMMENT_KEY + '*');

        if(!likeKeys.isEmpty()){
            // 更新点赞数
            REFRESH_POOL.submit(() -> new RefreshTask(likeKeys,0));
        }

        if (!collectKeys.isEmpty()){
            // 更新收藏数
            REFRESH_POOL.submit(() -> new RefreshTask(collectKeys,1));
        }

        if (!commentKeys.isEmpty()){
            // 更新评论数
            REFRESH_POOL.submit(() -> new RefreshTask(commentKeys,2));
        }
    }

    /**
     * scan查找所有key
     *
     * @param pattern 匹配模式
     * @return key的set集合
     */
    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        // 设置 SCAN 命令
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern) // 匹配模式
                .count(100) // 每次扫描 100 条
                .build();

        // 使用 RedisTemplate 执行 SCAN
        try (Cursor<byte[]> scanCursor = stringRedisTemplate.execute((RedisCallback<Cursor<byte[]>>) conn -> conn.scan(options), true)) {
            // 执行扫描并获取结果
            if (scanCursor != null) {
                while (scanCursor.hasNext()) {
                    // 将字节数组转换为字符串
                    String key = new String(scanCursor.next(), StandardCharsets.UTF_8);
                    keys.add(key);
                }
            }
        }

        return keys;
    }

    /**
     * 刷新数据到 mysql 中的任务
     */
    private class RefreshTask implements Runnable{

        /**
         * key的列表
         */
        private Set<String> keys;

        /**
         * 类型
         * 0 like
         * 1 collect
         * 2 comment
         */
        private Integer type;

        public RefreshTask(Set<String> keys, Integer type) {
            this.keys = keys;
            this.type = type;
        }

        @Override
        public void run() {
            switch (type) {
                case 0:
                    // 更新点赞数
                    for (String likeKey : keys) {
                        // 获取 passageId
                        String sub = likeKey.substring(RedisKeyConstant.STRING_LIKE_KEY.length());
                        long videoId = Long.parseLong(sub);
                        // 创建新的文章对象
                        PassageDO passageDO = new PassageDO();
                        passageDO.setId(videoId);
                        // 获取点赞数
                        String likes = stringRedisTemplate.opsForValue().get(likeKey);
                        long likesNum = Long.parseLong(likes == null ? "0" : likes);
                        passageDO.setLikes(likesNum);
                        // 刷新到数据库
                        passageMapper.updateById(passageDO);
                    }
                    break;
                case 1:
                    // 更新收藏数
                    for (String collectKey : keys) {
                        // 获取 passageId
                        String sub = collectKey.substring(RedisKeyConstant.STRING_COLLECT_KEY.length());
                        long passageId = Long.parseLong(sub);
                        // 创建新的文章对象
                        PassageDO passageDO = new PassageDO();
                        passageDO.setId(passageId);
                        // 获取收藏数
                        String collects = stringRedisTemplate.opsForValue().get(collectKey);
                        long collectsNum = Long.parseLong(collects == null ? "0" : collects);
                        passageDO.setCollection(collectsNum);
                        // 更新数据库
                        passageMapper.updateById(passageDO);
                    }
                    break;
                case 2:
                    // 更新评论数
                    for (String commentKey : keys) {
                        // 获取 passageId
                        String sub = commentKey.substring(RedisKeyConstant.STRING_COLLECT_KEY.length());
                        long passageId = Long.parseLong(sub);
                        // 创建新的文章对象
                        PassageDO passageDO = new PassageDO();
                        passageDO.setId(passageId);
                        // 获取收藏数
                        String comments = stringRedisTemplate.opsForValue().get(commentKey);
                        long commentNum = Long.parseLong(comments == null ? "0" : comments);
                        passageDO.setCommentCounts(commentNum);
                        // 更新数据库
                        passageMapper.updateById(passageDO);
                    }
                    break;
            }
        }
    }
}
