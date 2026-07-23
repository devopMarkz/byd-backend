package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DashboardResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.dominio.entidade.Receita;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class CalcularDashboardFinanceiro {

    private final ReceitaRepositorio receitaRepositorio;
    private final DespesaRepositorio despesaRepositorio;
    private final RecargaRepositorio recargaRepositorio;
    private final JornadaOperacionalRepositorio jornadaRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public CalcularDashboardFinanceiro(ReceitaRepositorio receitaRepositorio,
                                       DespesaRepositorio despesaRepositorio,
                                       RecargaRepositorio recargaRepositorio,
                                       JornadaOperacionalRepositorio jornadaRepositorio,
                                       CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
        this.despesaRepositorio = despesaRepositorio;
        this.recargaRepositorio = recargaRepositorio;
        this.jornadaRepositorio = jornadaRepositorio;
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public DashboardResponse executar(LocalDate data) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<Receita> receitas = receitaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), data, data);
        List<Despesa> despesas = despesaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), data, data);
        List<Recarga> recargas = recargaRepositorio.findByDataBetweenAndAtivoTrueOrderByDataDesc(data, data);
        List<JornadaOperacional> jornadas = jornadaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(usuario.getTenantId(), data, data);
        List<CategoriaDespesa> categorias = categoriaDespesaRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId());

        BigDecimal faturamento = receitas.stream()
                .map(r -> r.getValor().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesasOperacionais = CalculadoraFinanceira.calcularDespesasOperacionaisSemEnergia(despesas, categorias);
        BigDecimal custoEnergeticoRecargas = recargas.stream()
                .map(r -> r.getCusto().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal custoEnergetico = CalculadoraFinanceira.calcularCustoEnergeticoEfetivo(custoEnergeticoRecargas, despesas, categorias);

        BigDecimal despesasTotais = despesas.stream()
                .map(d -> d.getValor().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal quilometrosPercorridos = receitas.stream()
                .map(Receita::getQuilometrosRodados)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal kWhConsumidos = recargas.stream()
                .map(r -> r.getEnergiaConsumida().getKWh())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoPorKm = BigDecimal.ZERO;
        if (quilometrosPercorridos.compareTo(BigDecimal.ZERO) > 0) {
            custoPorKm = custoEnergetico.divide(quilometrosPercorridos, 4, RoundingMode.HALF_UP);
        }

        BigDecimal horasTrabalhadas = receitas.stream()
                .map(Receita::getHorasTrabalhadas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ganhoPorHora = dividirSeguro(faturamento, horasTrabalhadas);

        BigDecimal lucroLiquido = faturamento.subtract(despesasOperacionais).subtract(custoEnergetico);

        int totalViagens = receitas.stream().map(Receita::getQuantidadeViagens).reduce(0, Integer::sum);

        BigDecimal faturamentoMedioPorHora = dividirSeguro(faturamento, horasTrabalhadas);
        BigDecimal faturamentoMedioPorKm = dividirSeguro(faturamento, quilometrosPercorridos);
        BigDecimal custoPorViagem = totalViagens > 0
                ? custoEnergetico.divide(BigDecimal.valueOf(totalViagens), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal custoPorHora = dividirSeguro(custoEnergetico, horasTrabalhadas);

        BigDecimal percentualDespesasSobreReceita = faturamento.compareTo(BigDecimal.ZERO) > 0
                ? despesasOperacionais.add(custoEnergetico)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(faturamento, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal metaDiaria = new BigDecimal("300.00");
        BigDecimal percentualMetaAtingida = metaDiaria.compareTo(BigDecimal.ZERO) > 0
                ? faturamento.multiply(BigDecimal.valueOf(100))
                        .divide(metaDiaria, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new DashboardResponse(
                data,
                faturamento,
                despesasTotais,
                lucroLiquido,
                custoEnergetico,
                custoPorKm,
                ganhoPorHora,
                quilometrosPercorridos,
                kWhConsumidos,
                totalViagens,
                horasTrabalhadas,
                faturamentoMedioPorHora,
                faturamentoMedioPorKm,
                custoPorViagem,
                custoPorHora,
                percentualDespesasSobreReceita,
                metaDiaria,
                percentualMetaAtingida
        );
    }

    private long calcularMinutosTrabalhados(List<JornadaOperacional> jornadas) {
        long total = 0;
        for (JornadaOperacional jornada : jornadas) {
            if (jornada.getHorarioFim() != null && jornada.getHorarioInicio() != null) {
                total += Duration.between(jornada.getHorarioInicio(), jornada.getHorarioFim()).toMinutes();
            }
        }
        return total;
    }

    private BigDecimal dividirSeguro(BigDecimal dividendo, BigDecimal divisor) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividendo.divide(divisor, 4, RoundingMode.HALF_UP);
    }
}
