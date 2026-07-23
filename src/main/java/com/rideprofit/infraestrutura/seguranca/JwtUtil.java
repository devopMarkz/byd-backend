package com.rideprofit.infraestrutura.seguranca;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.rideprofit.dominio.entidade.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtUtil {

    private static final String ISSUER = "ride-profit";
    private static final int HORAS_EXPIRACAO_ACCESS_TOKEN = 8;
    private static final int DIAS_EXPIRACAO_REFRESH_TOKEN = 7;
    private static final String CLAIM_TIPO = "tipo";
    private static final String TIPO_ACCESS = "access";
    private static final String TIPO_REFRESH = "refresh";

    @Value("${token.secret}")
    private String secret;

    public String gerarAccessToken(Usuario usuario) {
        return gerarToken(usuario, TIPO_ACCESS, HORAS_EXPIRACAO_ACCESS_TOKEN);
    }

    public String gerarRefreshToken(Usuario usuario) {
        return gerarToken(usuario, TIPO_REFRESH, DIAS_EXPIRACAO_REFRESH_TOKEN * 24);
    }

    private String gerarToken(Usuario usuario, String tipo, int horasExpiracao) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("tenantId", usuario.getTenantId().toString())
                    .withClaim("usuarioId", usuario.getId().toString())
                    .withClaim("role", usuario.getPerfil().name())
                    .withClaim(CLAIM_TIPO, tipo)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(gerarDataExpiracao(horasExpiracao))
                    .sign(algorithm);
        } catch (JWTCreationException | IllegalArgumentException e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String validarAccessToken(String token) {
        return validarToken(token, TIPO_ACCESS);
    }

    public String validarRefreshToken(String token) {
        return validarToken(token, TIPO_REFRESH);
    }

    private String validarToken(String token, String tipoEsperado) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            var decoded = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(sanitizarToken(token));

            String tipo = decoded.getClaim(CLAIM_TIPO).asString();
            if (!tipoEsperado.equals(tipo)) {
                throw new RuntimeException("Tipo de token invalido");
            }

            return decoded.getSubject();
        } catch (TokenExpiredException e) {
            throw new RuntimeException("Token expirado");
        } catch (JWTVerificationException | IllegalArgumentException e) {
            throw new RuntimeException("Token invalido");
        }
    }

    private Instant gerarDataExpiracao(int horas) {
        return Instant.now().plusSeconds(horas * 60L * 60L);
    }

    private String sanitizarToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token nulo");
        }
        String t = token.trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException("token vazio");
        }
        String prefixo = "Bearer ";
        if (t.regionMatches(true, 0, prefixo, 0, prefixo.length())) {
            return t.substring(prefixo.length()).trim();
        }
        return t;
    }
}
