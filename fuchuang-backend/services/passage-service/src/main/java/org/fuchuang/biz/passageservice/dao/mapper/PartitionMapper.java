package org.fuchuang.biz.passageservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.fuchuang.biz.passageservice.dao.entity.PartitionDO;

/**
 * 文章分区持久层
 */
public interface PartitionMapper extends BaseMapper<PartitionDO> {

    /**
     * 获取分区信息
     */
    PartitionDO getPartitionInfo();
}
