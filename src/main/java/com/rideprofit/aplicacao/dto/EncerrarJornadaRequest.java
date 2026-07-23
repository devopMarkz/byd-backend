package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record EncerrarJornadaRequest(
        @NotNull UUID jornadaId,
        @NotNull LocalTime horarioFim,
        @NotNull BigDecimal odometroFinal,
        @NotNull BigDecimal percentualBateriaFinal
) {
}
