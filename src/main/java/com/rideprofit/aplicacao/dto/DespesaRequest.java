package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DespesaRequest(
        @NotNull @Positive BigDecimal valor,
        @NotNull UUID categoriaSaidaId,
        UUID formaPagamentoId,
        @NotNull LocalDate data,
        UUID jornadaId,
        @NotBlank String diaSemana,
        @NotBlank String tipoGasto,
        String descricao,
        String itemManutencao,
        String notaFiscalBase64,
        String notaFiscalNome,
        String notaFiscalTipo,
        String observacao
) {
}
