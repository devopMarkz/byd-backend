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
public class Quilometragem {

    private BigDecimal valor;

    public static Quilometragem emQuilometros(BigDecimal valor) {
        return new Quilometragem(valor);
    }

    public Quilometragem subtrair(Quilometragem outro) {
        return new Quilometragem(this.valor.subtract(outro.valor));
    }

    public BigDecimal dividirPor(BigDecimal divisor) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.valor.divide(divisor, 4, RoundingMode.HALF_UP);
    }
}
