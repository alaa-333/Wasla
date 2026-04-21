package com.example.wasla.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();

        // No token provided
        if (query == null || !query.startsWith("token=")) {
            log.warn("WebSocket connection rejected: No token provided");
            return false;
        }

        String token = query.substring(6);

        try {
            // Validate token
            if (jwtService.isTokenExpired(token)) {
                log.warn("WebSocket connection rejected: Token expired");
                return false;
            }

            // Extract username and store in attributes
            String username = jwtService.extractUsername(token);
            attributes.put("username", username);
            
            log.debug("WebSocket connection accepted for user: {}", username);
            return true;

        } catch (Exception e) {
            log.error("WebSocket connection rejected: Invalid token", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
