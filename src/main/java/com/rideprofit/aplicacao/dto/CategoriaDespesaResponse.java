package com.rideprofit.aplicacao.dto;

import java.util.UUID;

public record CategoriaDespesaResponse(
        UUID id,
        String nome,
        String descricao,
        boolean padrao
) {
}
