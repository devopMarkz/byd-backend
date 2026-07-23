package com.rideprofit.aplicacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DespesaResponse(
        UUID id,
        BigDecimal valor,
        UUID categoriaSaidaId,
        String categoriaSaidaNome,
        UUID formaPagamentoId,
        String formaPagamentoNome,
        LocalDate data,
        UUID jornadaId,
        String diaSemana,
        String tipoGasto,
        String descricao,
        String itemManutencao,
        boolean possuiNotaFiscal,
        String notaFiscalNome,
        String notaFiscalTipo,
        String observacao
) {
}
