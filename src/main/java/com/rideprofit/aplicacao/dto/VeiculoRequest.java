package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record VeiculoRequest(
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Positive Integer ano,
        @NotBlank String tipo,
        @NotNull @Positive BigDecimal capacidadeBateriaKwh,
        @NotNull @Positive BigDecimal autonomiaKm,
        @NotNull @Positive BigDecimal consumoMedioKwhKm
) {
}
