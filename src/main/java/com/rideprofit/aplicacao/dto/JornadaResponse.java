package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record JornadaResponse(
        UUID id,
        UUID veiculoId,
        LocalDate data,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        BigDecimal odometroInicial,
        BigDecimal odometroFinal,
        BigDecimal percentualBateriaInicial,
        BigDecimal percentualBateriaFinal,
        String status
) {
}
