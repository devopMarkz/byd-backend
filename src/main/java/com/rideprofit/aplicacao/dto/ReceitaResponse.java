package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ReceitaResponse(
        UUID id,
        BigDecimal valor,
        LocalDate data,
        LocalTime horario,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        UUID origemId,
        String origemNome,
        UUID jornadaId,
        String plataforma,
        String diaSemana,
        Integer quantidadeViagens,
        BigDecimal quilometrosRodados,
        BigDecimal horasTrabalhadas,
        String observacao,
        String notaFiscalNome,
        String notaFiscalTipo,
        boolean possuiNotaFiscal
) {
}
