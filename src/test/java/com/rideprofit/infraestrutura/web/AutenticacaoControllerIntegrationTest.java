package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.dto.LoginRequest;
import com.rideprofit.aplicacao.dto.TokenResponse;
import com.rideprofit.dominio.entidade.Perfil;
import com.rideprofit.dominio.entidade.Tenant;
import com.rideprofit.dominio.entidade.Usuario;
import java.math.BigDecimal;
import com.rideprofit.infraestrutura.persistencia.TenantRepositorio;
import com.rideprofit.infraestrutura.persistencia.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AutenticacaoControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private TenantRepositorio tenantRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepositorio.deleteAll();
        tenantRepositorio.deleteAll();

        Tenant tenant = new Tenant();
        tenant.setNome("Teste");
        tenant.setTarifaEnergiaKwh(new BigDecimal("0.9500"));
        tenant = tenantRepositorio.save(tenant);

        Usuario usuario = new Usuario();
        usuario.setTenantId(tenant.getId());
        usuario.setNome("Motorista Teste");
        usuario.setEmail("motorista@teste.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setPerfil(Perfil.ROLE_MOTORISTA);
        usuarioRepositorio.save(usuario);
    }

    @Test
    void deveAutenticarUsuarioERetornarTokenValido() {
        LoginRequest login = new LoginRequest("motorista@teste.com", "senha123");

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                "/auth/login", login, TokenResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().email()).isEqualTo("motorista@teste.com");
    }

    @Test
    void deveBloquearAcessoSemToken() {
        ResponseEntity<String> response = restTemplate.getForEntity("/dashboard/financeiro?data=2026-07-17", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void devePermitirAcessoComTokenValido() {
        LoginRequest login = new LoginRequest("motorista@teste.com", "senha123");
        TokenResponse token = restTemplate.postForEntity("/auth/login", login, TokenResponse.class).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.accessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/dashboard/financeiro?data=2026-07-17", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
