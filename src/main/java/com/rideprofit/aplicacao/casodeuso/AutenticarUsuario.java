package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.LoginRequest;
import com.rideprofit.aplicacao.dto.TokenResponse;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.seguranca.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuario {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AutenticarUsuario(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponse executar(LoginRequest request) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        Usuario usuario = (Usuario) authentication.getPrincipal();

        String accessToken = jwtUtil.gerarAccessToken(usuario);
        String refreshToken = jwtUtil.gerarRefreshToken(usuario);

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                8L * 60 * 60 * 1000,
                usuario.getTenantId(),
                usuario.getId(),
                usuario.getEmail()
        );
    }
}
