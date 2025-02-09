package org.fuchuang.biz.userservice.service;

import org.fuchuang.biz.userservice.dto.req.UserLoginReqDTO;
import org.fuchuang.biz.userservice.dto.resp.UserLoginRespDTO;

/**
 * 用户登录相关接口
 */
public interface UserLoginService {

    /**
     * 用户登录
     * @param requestParam 用户登录参数
     * @return 用户基本信息
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);
}
