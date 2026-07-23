package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AtualizarJornadaRequest(
        @NotNull LocalDate data,
        @NotNull LocalTime horarioInicio,
        @NotNull LocalTime horarioFim
) {
}