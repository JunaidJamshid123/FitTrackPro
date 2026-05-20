package com.fitness.fittrackpro.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String body = ("{\"timestamp\":\"" + Instant.now()
                + "\",\"status\":403,\"error\":\"Forbidden\","
                + "\"message\":\"You do not have permission to access this resource\","
                + "\"path\":\"" + JsonEscape.escape(request.getRequestURI()) + "\"}");
        response.getWriter().write(body);
    }
}
