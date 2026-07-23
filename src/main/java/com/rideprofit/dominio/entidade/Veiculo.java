package com.rideprofit.dominio.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "veiculo")
@Getter
@Setter
public class Veiculo extends EntidadeBase {

    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "capacidade_bateria_kwh", nullable = false, precision = 10, scale = 3)
    private BigDecimal capacidadeBateriaKwh;

    @Column(name = "autonomia_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal autonomiaKm;

    @Column(name = "consumo_medio_kwh_km", nullable = false, precision = 10, scale = 4)
    private BigDecimal consumoMedioKwhKm;
}
