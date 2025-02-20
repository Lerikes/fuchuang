package org.fuchuang.biz.passageservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * lua脚本配置
 */
@Configuration
public class RedisScriptConfiguration {

    /**
     * 文章点赞脚本
     * @return DefaultRedisScript<Long>
     */
    @Bean
    public DefaultRedisScript<Long> passageLikeScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("lua/like.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
