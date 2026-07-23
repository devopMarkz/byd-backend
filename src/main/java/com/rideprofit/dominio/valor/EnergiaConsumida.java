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
public class EnergiaConsumida {

    private BigDecimal kWh;

    public static EnergiaConsumida emKwh(BigDecimal kWh) {
        return new EnergiaConsumida(kWh);
    }

    public Dinheiro calcularCusto(TarifaEnergia tarifa) {
        return tarifa.aplicar(this.kWh);
    }

    public BigDecimal dividirPorQuilometragem(Quilometragem quilometragem) {
        if (quilometragem == null || quilometragem.getValor() == null || quilometragem.getValor().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.kWh.divide(quilometragem.getValor(), 4, RoundingMode.HALF_UP);
    }
}
