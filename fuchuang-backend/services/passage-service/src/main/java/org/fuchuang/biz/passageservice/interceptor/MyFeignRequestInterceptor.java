package org.fuchuang.biz.passageservice.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyFeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("userId", UserContext.getUserId());
    }
}
