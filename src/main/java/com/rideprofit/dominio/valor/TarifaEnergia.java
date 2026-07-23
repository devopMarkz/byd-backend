package com.rideprofit.dominio.valor;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class TarifaEnergia {

    private BigDecimal valorPorKwh;

    public static TarifaEnergia emReaisPorKwh(BigDecimal valorPorKwh) {
        if (valorPorKwh == null || valorPorKwh.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tarifa de energia deve ser maior ou igual a zero.");
        }
        return new TarifaEnergia(valorPorKwh);
    }

    public Dinheiro aplicar(BigDecimal kWh) {
        BigDecimal custo = this.valorPorKwh.multiply(kWh).setScale(2, RoundingMode.HALF_UP);
        return Dinheiro.emReais(custo);
    }
}
