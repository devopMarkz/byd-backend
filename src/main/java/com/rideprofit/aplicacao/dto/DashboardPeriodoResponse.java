package com.rideprofit.aplicacao.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record DashboardPeriodoResponse(LocalDate inicio, LocalDate fim, BigDecimal receita, BigDecimal despesas, BigDecimal saldo, EstatisticasPeriodoResponse estatisticas, List<OrigemDashboardResponse> receitasPorOrigem, ResumoJornadaResponse ultimaJornada) {}
