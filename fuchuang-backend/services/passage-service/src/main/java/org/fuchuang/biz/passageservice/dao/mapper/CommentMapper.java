package org.fuchuang.biz.passageservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.fuchuang.biz.passageservice.dao.entity.CommentDO;

/**
 * 评论持久层
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentDO> {
}
