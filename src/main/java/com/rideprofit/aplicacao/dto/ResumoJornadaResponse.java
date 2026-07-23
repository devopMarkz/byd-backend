package com.rideprofit.aplicacao.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
public record ResumoJornadaResponse(LocalDate data, LocalTime inicio, LocalTime fim, BigDecimal horasTrabalhadas, BigDecimal quilometrosPercorridos) {}
