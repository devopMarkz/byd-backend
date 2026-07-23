package com.rideprofit.infraestrutura.seguranca;

import com.auth0.jwt.JWT;
import com.rideprofit.infraestrutura.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    @PersistenceContext
    private EntityManager entityManager;

    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService usuarioDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public JwtFiltro(JwtUtil jwtUtil,
                     UsuarioDetailsService usuarioDetailsService,
                     CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtUtil = jwtUtil;
        this.usuarioDetailsService = usuarioDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && (path.equals("/api/auth/login") || path.equals("/api/auth/refresh"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = obterTokenDoHeader(request);
            UUID tenantId = null;

            if (token != null) {
                String email = jwtUtil.validarAccessToken(token);
                UserDetails usuario = usuarioDetailsService.loadUserByUsername(email);

                if (usuario.isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    tenantId = obterTenantId(token);
                    TenantContext.setTenantId(tenantId);
                }
            }

            aplicarTenantFilter(tenantId);
        } catch (RuntimeException e) {
            customAuthenticationEntryPoint.commence(request, response, new AuthenticationException(e.getMessage()) {});
            return;
        }

        filterChain.doFilter(request, response);
        TenantContext.limpar();
    }

    private void aplicarTenantFilter(UUID tenantId) {
        if (tenantId != null && entityManager != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId.toString());
        }
    }

    private String obterTokenDoHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.split(" ")[1];
    }

    private UUID obterTenantId(String token) {
        return UUID.fromString(JWT.decode(token).getClaim("tenantId").asString());
    }
}
