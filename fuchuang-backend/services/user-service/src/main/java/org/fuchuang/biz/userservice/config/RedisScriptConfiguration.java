package org.fuchuang.biz.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * lua脚本执行器配置
 */
@Configuration
public class RedisScriptConfiguration {

    /**
     * 用户注册脚本
     * @return DefaultRedisScript<Long>
     */
    @Bean
    public DefaultRedisScript<Long> registerRedisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("lua/register.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
