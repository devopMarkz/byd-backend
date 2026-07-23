package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RecargaResponse(
        UUID id,
        UUID veiculoId,
        UUID despesaId,
        LocalDate data,
        LocalTime horario,
        BigDecimal kwhConsumidos,
        BigDecimal tarifaKwh,
        BigDecimal custo,
        String localRecarga,
        String observacao,
        String notaFiscalNome,
        String notaFiscalTipo,
        boolean possuiNotaFiscal
) {
}
