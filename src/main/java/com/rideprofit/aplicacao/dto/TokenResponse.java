package com.rideprofit.aplicacao.dto;

import java.util.UUID;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        Long expiracao,
        UUID tenantId,
        UUID usuarioId,
        String email
) {
}
