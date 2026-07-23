package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardResponse(
        LocalDate data,
        BigDecimal faturamento,
        BigDecimal despesas,
        BigDecimal lucroLiquido,
        BigDecimal custoEnergetico,
        BigDecimal custoPorKm,
        BigDecimal ganhoPorHora,
        BigDecimal quilometrosPercorridos,
        BigDecimal kWhConsumidos,
        Integer totalViagens,
        BigDecimal horasTrabalhadas,
        BigDecimal faturamentoMedioPorHora,
        BigDecimal faturamentoMedioPorKm,
        BigDecimal custoPorViagem,
        BigDecimal custoPorHora,
        BigDecimal percentualDespesasSobreReceita,
        BigDecimal metaDiaria,
        BigDecimal percentualMetaAtingida
) {
}
