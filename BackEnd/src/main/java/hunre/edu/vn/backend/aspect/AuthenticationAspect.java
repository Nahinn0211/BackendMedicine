package hunre.edu.vn.backend.aspect;

import hunre.edu.vn.backend.annotation.RequireAuthentication;
import hunre.edu.vn.backend.exception.ForbiddenException;
import hunre.edu.vn.backend.exception.UnauthorizedException;
import hunre.edu.vn.backend.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class AuthenticationAspect {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthenticationAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Before("@annotation(requireAuthentication)")
    public void checkAuthentication(RequireAuthentication requireAuthentication, JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // Bỏ qua kiểm tra nếu là phương thức GET
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return;
        }

        // Lấy token từ request
        String token = extractTokenFromRequest();

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Token không hợp lệ");
        }

        // Kiểm tra vai trò nếu có
        String[] requiredRoles = requireAuthentication.roles();
        if (requiredRoles.length > 0) {
            String userRole = jwtTokenProvider.getRoleFromToken(token);
            boolean hasPermission = Arrays.stream(requiredRoles)
                    .anyMatch(role -> role.equals(userRole));

            if (!hasPermission) {
                throw new ForbiddenException("Bạn không có quyền thực hiện thao tác này");
            }
        }
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}