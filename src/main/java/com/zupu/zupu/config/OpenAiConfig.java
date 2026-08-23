package com.zupu.zupu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * OpenAI配置类
 * 用于创建和配置RestTemplate实例，用于调用DeepSeek API
 */
@Configuration
public class OpenAiConfig {

    /**
     * 创建RestTemplate实例
     * RestTemplate是Spring提供的用于发送HTTP请求的工具类
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        // 创建并返回RestTemplate实例
        return new RestTemplate();
    }
}
