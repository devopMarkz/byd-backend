package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record DashboardMensalResponse(
        YearMonth mes,
        BigDecimal faturamentoTotal,
        BigDecimal despesasTotais,
        BigDecimal lucroLiquidoTotal,
        BigDecimal quilometrosTotais,
        BigDecimal horasTrabalhadasTotais,
        Integer totalViagens,
        List<DiaResumoResponse> dias
) {
}
