package org.fuchuang.biz.userservice.service.handler.filter.user;

import org.fuchuang.biz.userservice.common.enums.UserChainMarkEnum;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.framework.starter.designpattern.chain.AbstractChainHandler;

/**
 * 用户注册责任链过滤器
 * @param <T> 泛型
 */
public interface UserRegisterCreateChainFilter<T extends UserRegisterReqDTO> extends AbstractChainHandler<UserRegisterReqDTO> {

    @Override
    default String mark(){
        return UserChainMarkEnum.USER_REGISTER_FILTER.name();
    }
}
