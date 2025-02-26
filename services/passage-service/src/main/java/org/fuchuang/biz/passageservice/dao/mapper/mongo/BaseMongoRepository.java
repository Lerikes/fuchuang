package org.fuchuang.biz.passageservice.dao.mapper.mongo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class BaseMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements IMongoRepository<T, ID> {
    protected final MongoOperations mongoOperations;

    protected final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }
    protected Class<T> getEntityClass() {
        return entityInformation.getJavaType();
    }


    @Override
    public T create(T entity) {
        return this.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        T t1 = this.getOne(id);
        BeanUtil.copyProperties(entity, t1, CopyOptions.create().ignoreNullValue());
        return this.save(t1);
    }

    @Override
    public List<ID> delete(Collection<ID> ids) {
        ids.forEach(id -> {
            T t = this.getOne(id);
            this.delete(t);
        });
        return (List<ID>) ids;
    }

    @SneakyThrows
    @Override
    public T getOne(ID id) {
        return this.findById(id)
                .orElseThrow(() -> new Exception("未找到记录:" + id));
    }

    @Override
    public Page<T> pageList(T entity, PageRequest pageRequest) {
        return this.findAll(Example.of(entity), pageRequest);
    }
}

