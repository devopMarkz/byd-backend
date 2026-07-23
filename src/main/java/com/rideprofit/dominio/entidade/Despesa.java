package com.rideprofit.dominio.entidade;

import com.rideprofit.dominio.valor.Dinheiro;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "despesa")
@Getter
@Setter
public class Despesa extends EntidadeBase {

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "jornada_id")
    private UUID jornadaId;

    @Column(name = "categoria_despesa_id", nullable = false)
    private UUID categoriaDespesaId;

    @Column(name = "categoria_saida_id", nullable = false)
    private UUID categoriaSaidaId;

    @Column(name = "forma_pagamento_id")
    private UUID formaPagamentoId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "valor", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "moeda", column = @Column(name = "moeda", nullable = false, length = 3))
    })
    private Dinheiro valor;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana;

    @Column(name = "tipo_gasto", nullable = false, length = 30)
    private String tipoGasto;

    @Column(name = "item_manutencao", length = 500)
    private String itemManutencao;

    @Column(name = "nota_fiscal")
    private byte[] notaFiscal;

    @Column(name = "nota_fiscal_nome", length = 255)
    private String notaFiscalNome;

    @Column(name = "nota_fiscal_tipo", length = 100)
    private String notaFiscalTipo;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "observacao", length = 1000)
    private String observacao;
}
