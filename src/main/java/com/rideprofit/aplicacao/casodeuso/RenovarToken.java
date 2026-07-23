package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.TokenResponse;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.seguranca.JwtUtil;
import com.rideprofit.infraestrutura.seguranca.UsuarioDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class RenovarToken {

    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService usuarioDetailsService;

    public RenovarToken(JwtUtil jwtUtil, UsuarioDetailsService usuarioDetailsService) {
        this.jwtUtil = jwtUtil;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    public TokenResponse executar(String refreshToken) {
        String email = jwtUtil.validarRefreshToken(refreshToken);
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);
        Usuario usuario = (Usuario) userDetails;

        String novoAccessToken = jwtUtil.gerarAccessToken(usuario);
        String novoRefreshToken = jwtUtil.gerarRefreshToken(usuario);

        return new TokenResponse(
                novoAccessToken,
                novoRefreshToken,
                "Bearer",
                8L * 60 * 60 * 1000,
                usuario.getTenantId(),
                usuario.getId(),
                usuario.getEmail()
        );
    }
}
