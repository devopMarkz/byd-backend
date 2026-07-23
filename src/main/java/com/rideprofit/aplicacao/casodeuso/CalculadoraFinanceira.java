package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Despesa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CalculadoraFinanceira {

    private static final String NOME_CATEGORIA_ENERGIA = "Energia eletrica";

    private CalculadoraFinanceira() {
    }

    public static BigDecimal calcularCustoEnergeticoEfetivo(BigDecimal custoRecargas,
                                                            List<Despesa> despesas,
                                                            List<CategoriaDespesa> categorias) {
        BigDecimal despesasEnergia = calcularDespesasCategoriaEnergia(despesas, categorias);

        if (custoRecargas.compareTo(BigDecimal.ZERO) > 0) {
            return custoRecargas;
        }

        return despesasEnergia;
    }

    public static BigDecimal calcularDespesasOperacionaisSemEnergia(List<Despesa> despesas,
                                                                    List<CategoriaDespesa> categorias) {
        Set<UUID> idsCategoriaEnergia = categorias.stream()
                .filter(c -> NOME_CATEGORIA_ENERGIA.equalsIgnoreCase(c.getNome()))
                .map(CategoriaDespesa::getId)
                .collect(Collectors.toSet());

        return despesas.stream()
                .filter(d -> !idsCategoriaEnergia.contains(d.getCategoriaDespesaId()))
                .map(d -> d.getValor().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calcularDespesasCategoriaEnergia(List<Despesa> despesas,
                                                             List<CategoriaDespesa> categorias) {
        Set<UUID> idsCategoriaEnergia = categorias.stream()
                .filter(c -> NOME_CATEGORIA_ENERGIA.equalsIgnoreCase(c.getNome()))
                .map(CategoriaDespesa::getId)
                .collect(Collectors.toSet());

        return despesas.stream()
                .filter(d -> idsCategoriaEnergia.contains(d.getCategoriaDespesaId()))
                .map(d -> d.getValor().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
