package org.fuchuang.biz.passageservice.dao.mapper.mongo;

import org.bson.types.ObjectId;
import org.fuchuang.biz.passageservice.dao.entity.mongo.CommentDO;

/**
 * 文章评论mongoDB持久层
 */
public interface CommentRepository extends IMongoRepository<CommentDO, ObjectId> {
}
