package org.fuchuang.biz.userservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.fuchuang.biz.userservice.dao.entity.UserDO;

/**
 * 用户持久层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
