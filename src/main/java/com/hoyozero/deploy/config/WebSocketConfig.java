package com.hoyozero.deploy.config;

import com.hoyozero.deploy.websocket.SshTerminalWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SshTerminalWebSocketHandler sshTerminalWebSocketHandler;

    public WebSocketConfig(SshTerminalWebSocketHandler sshTerminalWebSocketHandler) {
        this.sshTerminalWebSocketHandler = sshTerminalWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // SSH 终端 WebSocket
        registry.addHandler(sshTerminalWebSocketHandler, "/ws/ssh/terminal")
                .setAllowedOrigins("*");
    }
}
