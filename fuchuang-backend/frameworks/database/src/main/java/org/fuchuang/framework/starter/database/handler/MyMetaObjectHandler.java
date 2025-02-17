package org.fuchuang.framework.starter.database.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.fuchuang.framework.starter.common.enums.DelEnum;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 元数据填充
 */
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 数据新增时填充
     * @param metaObject 元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"delFlag", Integer.class, DelEnum.NORMAL.code());
    }

    /**
     * 数据修改时填充
     * @param metaObject 元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
    }
}
