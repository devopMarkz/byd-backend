package com.rideprofit.aplicacao.dto;
import java.math.BigDecimal;
public record EstatisticasPeriodoResponse(int totalViagens, BigDecimal horasTrabalhadas, BigDecimal quilometrosRodados, BigDecimal receitaPorViagem, BigDecimal receitaPorHora, BigDecimal receitaPorKm, BigDecimal lucroPorViagem, BigDecimal lucroPorHora, BigDecimal lucroPorKm) {}
