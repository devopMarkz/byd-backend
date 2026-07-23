package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DashboardMensalResponse;
import com.rideprofit.aplicacao.dto.DiaResumoResponse;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ObterDashboardMensal {

    private final ReceitaRepositorio receitaRepositorio;
    private final DespesaRepositorio despesaRepositorio;
    private final RecargaRepositorio recargaRepositorio;
    private final JornadaOperacionalRepositorio jornadaRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public ObterDashboardMensal(ReceitaRepositorio receitaRepositorio,
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

    public DashboardMensalResponse executar(YearMonth mesReferencia) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        LocalDate inicio = mesReferencia.atDay(1);
        LocalDate fim = mesReferencia.atEndOfMonth();

        List<Receita> receitas = receitaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);
        List<Despesa> despesas = despesaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);
        List<Recarga> recargas = recargaRepositorio.findByDataBetweenAndAtivoTrueOrderByDataDesc(inicio, fim);
        List<JornadaOperacional> jornadas = jornadaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(usuario.getTenantId(), inicio, fim);
        List<CategoriaDespesa> categorias = categoriaDespesaRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId());

        Map<LocalDate, List<Receita>> receitasPorDia = receitas.stream().collect(Collectors.groupingBy(Receita::getData));
        Map<LocalDate, List<Despesa>> despesasPorDia = despesas.stream().collect(Collectors.groupingBy(Despesa::getData));
        Map<LocalDate, List<Recarga>> recargasPorDia = recargas.stream().collect(Collectors.groupingBy(Recarga::getData));
        Map<LocalDate, List<JornadaOperacional>> jornadasPorDia = jornadas.stream().collect(Collectors.groupingBy(JornadaOperacional::getData));

        List<DiaResumoResponse> dias = new ArrayList<>();
        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        BigDecimal despesasOperacionaisTotais = BigDecimal.ZERO;
        BigDecimal custoEnergeticoTotal = BigDecimal.ZERO;
        BigDecimal quilometrosTotais = BigDecimal.ZERO;
        BigDecimal horasTrabalhadasTotais = BigDecimal.ZERO;
        int totalViagens = 0;

        for (LocalDate dia = inicio; !dia.isAfter(fim); dia = dia.plusDays(1)) {
            List<Receita> receitasDia = receitasPorDia.getOrDefault(dia, List.of());
            List<Despesa> despesasDia = despesasPorDia.getOrDefault(dia, List.of());
            List<Recarga> recargasDia = recargasPorDia.getOrDefault(dia, List.of());
            List<JornadaOperacional> jornadasDia = jornadasPorDia.getOrDefault(dia, List.of());

            BigDecimal faturamento = receitasDia.stream()
                    .map(r -> r.getValor().getValor())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal custoRecargasDia = recargasDia.stream()
                    .map(r -> r.getCusto().getValor())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal despesasOperacionais = CalculadoraFinanceira.calcularDespesasOperacionaisSemEnergia(despesasDia, categorias);
            BigDecimal custoEnergetico = CalculadoraFinanceira.calcularCustoEnergeticoEfetivo(custoRecargasDia, despesasDia, categorias);
            BigDecimal km = receitasDia.stream().map(Receita::getQuilometrosRodados).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal horas = receitasDia.stream().map(Receita::getHorasTrabalhadas).reduce(BigDecimal.ZERO, BigDecimal::add);
            Integer viagens = receitasDia.stream().map(Receita::getQuantidadeViagens).reduce(0, Integer::sum);

            dias.add(new DiaResumoResponse(
                    dia,
                    faturamento,
                    despesasOperacionais.add(custoEnergetico),
                    custoEnergetico,
                    faturamento.subtract(despesasOperacionais).subtract(custoEnergetico),
                    km,
                    horas,
                    viagens
            ));

            faturamentoTotal = faturamentoTotal.add(faturamento);
            despesasOperacionaisTotais = despesasOperacionaisTotais.add(despesasOperacionais);
            custoEnergeticoTotal = custoEnergeticoTotal.add(custoEnergetico);
            quilometrosTotais = quilometrosTotais.add(km);
            horasTrabalhadasTotais = horasTrabalhadasTotais.add(horas);
            totalViagens += viagens;
        }

        BigDecimal lucroLiquidoTotal = faturamentoTotal.subtract(despesasOperacionaisTotais).subtract(custoEnergeticoTotal);
        BigDecimal despesasTotais = despesasOperacionaisTotais.add(custoEnergeticoTotal);

        return new DashboardMensalResponse(
                mesReferencia,
                faturamentoTotal,
                despesasTotais,
                lucroLiquidoTotal,
                quilometrosTotais,
                horasTrabalhadasTotais,
                totalViagens,
                dias
        );
    }

    private BigDecimal calcularQuilometragem(List<JornadaOperacional> jornadas) {
        return jornadas.stream()
                .filter(j -> j.getOdometroFinal() != null && j.getOdometroInicial() != null)
                .map(j -> j.getOdometroFinal().subtract(j.getOdometroInicial()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularHoras(List<JornadaOperacional> jornadas) {
        long minutos = jornadas.stream()
                .filter(j -> j.getHorarioFim() != null && j.getHorarioInicio() != null)
                .mapToLong(j -> Duration.between(j.getHorarioInicio(), j.getHorarioFim()).toMinutes())
                .sum();
        return BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
