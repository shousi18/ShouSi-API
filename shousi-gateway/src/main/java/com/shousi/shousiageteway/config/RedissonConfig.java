package com.shousi.shousiageteway.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
        // 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        // 创建实例
        return Redisson.create(config);
    }
}
