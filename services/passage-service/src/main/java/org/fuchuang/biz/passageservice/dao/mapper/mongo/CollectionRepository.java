package org.fuchuang.biz.passageservice.dao.mapper.mongo;

import org.bson.types.ObjectId;
import org.fuchuang.biz.passageservice.dao.entity.mongo.CollectionDO;

/**
 * mongodb收藏持久层
 */
public interface CollectionRepository extends IMongoRepository<CollectionDO, ObjectId> {
}
