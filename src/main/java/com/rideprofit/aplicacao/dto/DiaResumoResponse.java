package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaResumoResponse(
        LocalDate data,
        BigDecimal faturamento,
        BigDecimal despesas,
        BigDecimal custoEnergetico,
        BigDecimal lucroLiquido,
        BigDecimal quilometrosPercorridos,
        BigDecimal horasTrabalhadas,
        Integer totalViagens
) {
}
