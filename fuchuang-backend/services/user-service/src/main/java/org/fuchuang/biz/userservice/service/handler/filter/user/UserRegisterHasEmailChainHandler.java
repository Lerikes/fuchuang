package org.fuchuang.biz.userservice.service.handler.filter.user;

import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.biz.userservice.service.UserLoginService;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.springframework.stereotype.Component;

/**
 * 用户注册邮箱唯一校验
 */
@Component
@RequiredArgsConstructor
public final class UserRegisterHasEmailChainHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO> {

    private final UserLoginService userLoginService;

    /**
     * 执行责任链
     * @param requestParam 责任链执行入参
     */
    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        if(!userLoginService.hasEmail(requestParam.getEmail())){
            throw new ClientException(UserRegisterErrorCodeEnum.MAIL_REGISTERED);
        }
    }

    /**
     * 设置优先级
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
