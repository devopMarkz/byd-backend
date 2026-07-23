package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record IniciarJornadaRequest(
        @NotNull UUID veiculoId,
        @NotNull LocalDate data,
        @NotNull LocalTime horarioInicio,
        @NotNull BigDecimal odometroInicial,
        @NotNull BigDecimal percentualBateriaInicial
) {
}
