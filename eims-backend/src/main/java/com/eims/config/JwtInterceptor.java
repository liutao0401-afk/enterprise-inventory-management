package com.eims.config;

import com.eims.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行登录接口
        if (request.getRequestURI().contains("/auth/login")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "未登录")));
            return false;
        }

        token = token.substring(7);

        try {
            if (jwtConfig.isTokenExpired(token)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "Token已过期")));
                return false;
            }

            // 将用户信息存入请求属性
            request.setAttribute("userId", jwtConfig.getUserIdFromToken(token));
            request.setAttribute("username", jwtConfig.getUsernameFromToken(token));
            request.setAttribute("roleCode", jwtConfig.getRoleCodeFromToken(token));

            return true;
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "Token无效")));
            return false;
        }
    }
}
