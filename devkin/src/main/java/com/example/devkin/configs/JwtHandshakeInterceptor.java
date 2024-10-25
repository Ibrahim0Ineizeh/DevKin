package com.example.devkin.configs;

import com.example.devkin.services.JwtService;
import com.example.devkin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract token from query parameters
        String token = request.getURI().getQuery().split("token=")[1];
        logger.warn(token);
        if (token != null) {
            String userEmail = jwtService.extractUsername(token);
            if (userEmail != null) {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(token, userDetails)) {
                    attributes.put("userDetails", userDetails);
                    return true;
                } else {
                    logger.warn("Invalid JWT token: {}", token);
                }
            } else {
                logger.warn("Username could not be extracted from the token");
            }
        } else {
            logger.warn("Authorization token is missing or does not contain a valid Bearer token");
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            logger.error("WebSocket handshake failed: {}", exception.getMessage());
        } else {
            logger.info("WebSocket handshake successful");
        }
    }
}
