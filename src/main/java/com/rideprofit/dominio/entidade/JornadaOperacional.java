package com.rideprofit.dominio.entidade;

import com.rideprofit.dominio.valor.PercentualBateria;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "jornada_operacional")
@Getter
@Setter
public class JornadaOperacional extends EntidadeBase {

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Column(name = "odometro_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal odometroInicial;

    @Column(name = "odometro_final", precision = 10, scale = 2)
    private BigDecimal odometroFinal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "percentual_bateria_inicial", nullable = false, precision = 5, scale = 2))
    })
    private PercentualBateria percentualBateriaInicial;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "percentual_bateria_final", precision = 5, scale = 2))
    })
    private PercentualBateria percentualBateriaFinal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusJornada status;

    public enum StatusJornada {
        EM_ANDAMENTO,
        ENCERRADA
    }
}
