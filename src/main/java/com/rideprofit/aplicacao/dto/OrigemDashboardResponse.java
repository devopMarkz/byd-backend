package com.rideprofit.aplicacao.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record OrigemDashboardResponse(UUID id, String nome, String imagemBase64, BigDecimal receita, BigDecimal percentualDaMaior) {}
