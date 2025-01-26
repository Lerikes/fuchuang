package org.fuchuang.frameworks.starter.user.config;

import org.fuchuang.frameworks.starter.user.core.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static org.fuchuang.framework.starter.bases.constant.FilterOrderConstant.USER_TRANSMIT_FILTER_ORDER;

/**
 * 用户配置自动装配
 */
public class UserAutoConfiguration {

    /**
     * 用户信息传递过滤器
     * @return 过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(USER_TRANSMIT_FILTER_ORDER);
        return registration;
    }
}
