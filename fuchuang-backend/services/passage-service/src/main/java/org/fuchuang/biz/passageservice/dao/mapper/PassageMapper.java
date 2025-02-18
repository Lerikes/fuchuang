package org.fuchuang.biz.passageservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;

import java.util.List;

/**
 * 文章持久层
 */
public interface PassageMapper extends BaseMapper<PassageDO> {

    /**
     * 获取一级页面
     */
    @Select("SELECT id, `partition`, title, create_time FROM tbl_passage ORDER BY `partition`, create_time DESC LIMIT 10")
    List<PassageDO> getFirstPassageInfo();
}
