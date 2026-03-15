package com.mobileshop.security;

import com.mobileshop.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // always clear first
        TenantContext.clear();

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {
                String email    = jwtService.extractEmail(token);
                String tenantId = jwtService.extractTenantId(token);
                String role     = jwtService.extractRole(token);
                String type     = jwtService.extractType(token);

                // only set tenant context for USER tokens, not ADMIN
                if ("USER".equals(type) &&
                        tenantId != null && !tenantId.isBlank()) {
                    TenantContext.setCurrentTenant(tenantId);
                }

                var auth = new UsernamePasswordAuthenticationToken(
                        email, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // always clear after request
            TenantContext.clear();
        }
    }
}