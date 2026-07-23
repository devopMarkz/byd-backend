package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;

public record DespesaPorCategoriaResponse(
        String categoria,
        BigDecimal total,
        Double percentual
) {
}
