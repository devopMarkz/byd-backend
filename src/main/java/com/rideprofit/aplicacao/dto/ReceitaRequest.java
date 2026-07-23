package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ReceitaRequest(
        @NotNull @Positive BigDecimal valor,
        @NotNull LocalDate data,
        @NotNull LocalTime horario,
        @NotNull LocalDateTime dataHoraInicio,
        @NotNull LocalDateTime dataHoraFim,
        @NotNull UUID origemId,
        UUID jornadaId,
        String plataforma,
        @NotBlank String diaSemana,
        @NotNull @PositiveOrZero Integer quantidadeViagens,
        @NotNull @DecimalMin("0.0") BigDecimal quilometrosRodados,
        @NotNull @DecimalMin("0.0") BigDecimal horasTrabalhadas,
        String observacao,
        String notaFiscalBase64,
        String notaFiscalNome,
        String notaFiscalTipo
) {
}
