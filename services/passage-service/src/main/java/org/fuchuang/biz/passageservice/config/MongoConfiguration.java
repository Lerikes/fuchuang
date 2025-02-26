package org.fuchuang.biz.passageservice.config;

import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.passageservice.dao.mapper.mongo.BaseMongoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * mongo配置类
 */
@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(repositoryBaseClass = BaseMongoRepository.class,basePackages = "org.fuchuang.biz.passageservice.dao.mapper.mongo")
public class MongoConfiguration {

    private final MongoDatabaseFactory mongoDatabaseFactory;
    private final MongoMappingContext mongoMappingContext;

    @Bean
    public MappingMongoConverter mappingMongoConverter() {
        mongoMappingContext.setAutoIndexCreation(true);
        mongoMappingContext.afterPropertiesSet();
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        // 此处是去除插入数据库的 _class 字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;

    }

    /**
     * 开启事务
     * @return MongoTransactionManager
     */
    @Bean
    public MongoTransactionManager mongoTransactionManager() {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }
}
