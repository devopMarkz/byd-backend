package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSemanalResponse(
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal faturamentoTotal,
        BigDecimal despesasTotais,
        BigDecimal lucroLiquidoTotal,
        BigDecimal quilometrosTotais,
        BigDecimal horasTrabalhadasTotais,
        Integer totalViagens,
        List<DiaResumoResponse> dias
) {
}
