package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TarifaEnergiaRequest(
        @NotNull @Positive BigDecimal tarifaEnergiaKwh
) {
}
