package com.rideprofit.dominio.valor;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class PercentualBateria {

    private BigDecimal valor;

    public static PercentualBateria de(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0 || valor.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentual da bateria deve estar entre 0 e 100.");
        }
        return new PercentualBateria(valor);
    }

    public BigDecimal diferenca(PercentualBateria outro) {
        return this.valor.subtract(outro.valor);
    }
}
