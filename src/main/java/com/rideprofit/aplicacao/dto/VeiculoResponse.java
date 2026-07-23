package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VeiculoResponse(
        UUID id,
        String marca,
        String modelo,
        Integer ano,
        String tipo,
        BigDecimal capacidadeBateriaKwh,
        BigDecimal autonomiaKm,
        BigDecimal consumoMedioKwhKm
) {
}
