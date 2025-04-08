package hunre.edu.vn.backend.interceptor;

import hunre.edu.vn.backend.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Service xác thực token

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization == null || authorization.isEmpty()) {
                throw new AccessDeniedException("Missing Authorization header");
            }

            String token = authorization.get(0).replace("Bearer ", "");

            try {
                // Xác thực token
                if (!jwtTokenProvider.validateToken(token)) {
                    throw new AccessDeniedException("Invalid token");
                }
            } catch (Exception e) {
                throw new AccessDeniedException("Token validation failed", e);
            }
        }

        return message;
    }
}