package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecargaRequest(
        @NotNull LocalDate data,
        @NotNull @Positive BigDecimal valor,
        @NotNull @Positive BigDecimal kwhConsumidos,
        String localRecarga,
        String observacao,
        String notaFiscalBase64,
        String notaFiscalNome,
        String notaFiscalTipo
) {
}
