package com.rideprofit.dominio.entidade;

import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.dominio.valor.EnergiaConsumida;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "recarga")
@Getter
@Setter
public class Recarga extends EntidadeBase {

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "jornada_id")
    private UUID jornadaId;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    @Column(name = "percentual_inicial", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualInicial;

    @Column(name = "percentual_final", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualFinal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "kWh", column = @Column(name = "kwh_consumidos", nullable = false, precision = 10, scale = 3))
    })
    private EnergiaConsumida energiaConsumida;

    @Column(name = "tarifa_kwh", nullable = false, precision = 10, scale = 4)
    private BigDecimal tarifaKwh;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "custo", nullable = false, precision = 12, scale = 2)),
            @AttributeOverride(name = "moeda", column = @Column(name = "moeda_custo", nullable = false, length = 3))
    })
    private Dinheiro custo;

    @Column(name = "local_recarga", length = 200)
    private String localRecarga;

    @Column(name = "observacao", length = 1000)
    private String observacao;

    @Column(name = "nota_fiscal")
    private byte[] notaFiscal;

    @Column(name = "nota_fiscal_nome", length = 255)
    private String notaFiscalNome;

    @Column(name = "nota_fiscal_tipo", length = 100)
    private String notaFiscalTipo;

    @Column(name = "despesa_id")
    private UUID despesaId;
}
