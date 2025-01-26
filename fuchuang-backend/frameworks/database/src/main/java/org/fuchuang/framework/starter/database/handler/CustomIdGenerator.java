package org.fuchuang.framework.starter.database.handler;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.fuchuang.framework.starter.distributedid.toolkit.SnowflakeIdUtil;

/**
 * 自定义雪花算法生产器
 */
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
