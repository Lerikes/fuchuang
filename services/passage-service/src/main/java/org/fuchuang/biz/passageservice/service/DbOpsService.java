package org.fuchuang.biz.passageservice.service;

/**
 * 数据库操作接口
 */
public interface DbOpsService {

    /**
     * 安全的增加一个 int 类型的值
     *
     * @param key 视频id
     * @param num 点赞数
     */
    void addIntSafely(String key, int num);

    /**
     * 将数据异步插入 mongoDB 中
     *
     * @param userId  用户id
     * @param passageId 文章id
     * @param type    操作类型(1 点赞 2收藏 3评论)
     * @param ops     添加到对应字段的参数
     */
    void insertIntoMongoDB(String userId, String passageId, int type, Object ops);

    /**
     * 要是发现redis中like字段过期，则从数据库中查询数据返回，并同时把此视频所有字段刷新到redis
     * @param passageId 文章id
     * @return 点赞数
     */
    Long getSumFromDB(String passageId);
}
