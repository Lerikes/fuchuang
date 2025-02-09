package org.fuchuang.biz.userservice.service.handler.filter.user;

import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.userservice.common.constant.UserConstant;
import org.fuchuang.biz.userservice.common.enums.UserRegisterErrorCodeEnum;
import org.fuchuang.biz.userservice.dto.req.UserRegisterReqDTO;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.springframework.stereotype.Component;

/**
 * 用户注册密码校验
 */
@Component
@RequiredArgsConstructor
public final class UserRegisterPasswordVerifyHandler implements UserRegisterCreateChainFilter<UserRegisterReqDTO>{

    @Override
    public void handler(UserRegisterReqDTO requestParam) {
        String password = requestParam.getPassword();
        String repeatedPassword = requestParam.getRepeatedPassword();
        // 校验密码是否合法
        if (password.length() < UserConstant.PASSWORD_MIN_LENGTH || password.length() > UserConstant.PASSWORD_MAX_LENGTH) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_ILLEGAL);
        }
        // 校验两次密码是否一致
        if (!password.equals(repeatedPassword)) {
            throw new ClientException(UserRegisterErrorCodeEnum.PASSWORD_NOT_MATCH);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
