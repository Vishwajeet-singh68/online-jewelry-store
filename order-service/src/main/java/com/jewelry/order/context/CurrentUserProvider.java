package com.jewelry.order.context;

import com.jewelry.order.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private static final String X_USER_ID_HEADER = "X-User-Id";

    public Long getCurrentUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader(X_USER_ID_HEADER);
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new BadRequestException("Missing trusted user identity header: " + X_USER_ID_HEADER);
        }

        try {
            return Long.parseLong(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid user identity header format: " + userIdHeader);
        }
    }
}
