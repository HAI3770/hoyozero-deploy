package com.hoyozero.deploy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket Server 配置
 * 用于支持 @ServerEndpoint 注解的 WebSocket
 */
@Configuration
public class WebSocketServerConfig {

    /**
     * 注入 ServerEndpointExporter
     * 这个 Bean 会自动注册使用了 @ServerEndpoint 注解的 WebSocket 端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
