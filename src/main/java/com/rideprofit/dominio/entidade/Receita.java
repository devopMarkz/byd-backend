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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "receita")
@Getter
@Setter
public class Receita extends EntidadeBase {

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "jornada_id")
    private UUID jornadaId;

    @Column(name = "origem_id", nullable = false)
    private UUID origemId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "valor", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "moeda", column = @Column(name = "moeda", nullable = false, length = 3))
    })
    private Dinheiro valor;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana;

    @Column(name = "quantidade_viagens", nullable = false)
    private Integer quantidadeViagens;

    @Column(name = "quilometros_rodados", nullable = false, precision = 10, scale = 1)
    private java.math.BigDecimal quilometrosRodados;

    @Column(name = "horas_trabalhadas", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal horasTrabalhadas;

    @Column(name = "plataforma", length = 50)
    private String plataforma;

    @Column(name = "observacao", length = 1000)
    private String observacao;

    @Column(name = "nota_fiscal")
    private byte[] notaFiscal;

    @Column(name = "nota_fiscal_nome", length = 255)
    private String notaFiscalNome;

    @Column(name = "nota_fiscal_tipo", length = 100)
    private String notaFiscalTipo;
}
