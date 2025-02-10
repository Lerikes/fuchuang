package org.fuchuang.biz.userservice.service.handler.filter.user;

import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户注册参数必填检验
 */
@Component
public final class UserRegisterParamNotNullChainHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO> {

    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        if (Objects.isNull(requestParam.getPassword())) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_NOTNULL);
        } else if (Objects.isNull(requestParam.getPhone())) {
            throw new ClientException(UserRegisterErrorCodeEnum.PHONE_NOTNULL);
        } else if (Objects.isNull(requestParam.getEmail())) {
            throw new ClientException(UserRegisterErrorCodeEnum.MAIL_NOTNULL);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
