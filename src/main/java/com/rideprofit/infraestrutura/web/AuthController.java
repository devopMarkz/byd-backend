package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.AutenticarUsuario;
import com.rideprofit.aplicacao.casodeuso.RenovarToken;
import com.rideprofit.aplicacao.dto.LoginRequest;
import com.rideprofit.aplicacao.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticarUsuario autenticarUsuario;
    private final RenovarToken renovarToken;

    public AuthController(AutenticarUsuario autenticarUsuario, RenovarToken renovarToken) {
        this.autenticarUsuario = autenticarUsuario;
        this.renovarToken = renovarToken;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticarUsuario.executar(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String authorization) {
        String refreshToken = authorization.replace("Bearer ", "");
        return ResponseEntity.ok(renovarToken.executar(refreshToken));
    }
}
