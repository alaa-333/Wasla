package com.example.wasla.auth.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/*
    we use this class to
*  translate that username into a Spring Principal —
*  the standard Java representation of an authenticated identity.
*  You do this with a DefaultHandshakeHandler
* */
@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {


    // Spring calls this method to determine the Principal for the new session.
    // We read the username that the interceptor stored in attributes
    // and wrap it in a simple Principal implementation.
    @Override
    public Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String username = (String) attributes.get("username");

        if (username == null) {
            return null;
        }

        return () -> username;
    }


}
