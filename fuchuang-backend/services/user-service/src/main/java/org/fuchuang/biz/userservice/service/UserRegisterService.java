package org.fuchuang.biz.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.fuchuang.biz.userservice.dao.entity.UserDO;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;

/**
 * 用户注册接口
 */
public interface UserRegisterService extends IService<UserDO>{
    /**
     * 注册
     */
    String register(UserRegisterReqDTO userRegisterReqDTO);
}
