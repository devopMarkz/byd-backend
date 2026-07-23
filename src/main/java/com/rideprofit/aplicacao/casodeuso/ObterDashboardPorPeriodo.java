package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.*;
import com.rideprofit.dominio.entidade.*;
import com.rideprofit.infraestrutura.persistencia.*;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ObterDashboardPorPeriodo {
    private final ReceitaRepositorio receitas; private final DespesaRepositorio despesas; private final JornadaOperacionalRepositorio jornadas; private final OrigemRepositorio origens; private final RecargaRepositorio recargas;
    public ObterDashboardPorPeriodo(ReceitaRepositorio receitas, DespesaRepositorio despesas, JornadaOperacionalRepositorio jornadas, OrigemRepositorio origens, RecargaRepositorio recargas){this.receitas=receitas;this.despesas=despesas;this.jornadas=jornadas;this.origens=origens;this.recargas=recargas;}
    public DashboardPeriodoResponse executar(LocalDate inicio, LocalDate fim){
        if(fim.isBefore(inicio)) throw new IllegalArgumentException("A data final nao pode ser anterior a data inicial.");
        Usuario u=UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<Receita> listaReceitas=receitas.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(u.getTenantId(),inicio,fim);
        List<Despesa> listaDespesas=despesas.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(u.getTenantId(),inicio,fim);
        List<JornadaOperacional> listaJornadas=jornadas.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(u.getTenantId(),inicio,fim);
        BigDecimal receita=somarReceitas(listaReceitas);
        BigDecimal despesa=listaDespesas.stream().map(d->d.getValor().getValor()).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal saldo=receita.subtract(despesa);
        BigDecimal km=listaReceitas.stream().map(Receita::getQuilometrosRodados).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal horas=listaReceitas.stream().map(Receita::getHorasTrabalhadas).reduce(BigDecimal.ZERO, BigDecimal::add);
        int viagens=listaReceitas.stream().map(Receita::getQuantidadeViagens).reduce(0, Integer::sum);
        EstatisticasPeriodoResponse estatisticas=new EstatisticasPeriodoResponse(viagens,horas,km,dividir(receita,BigDecimal.valueOf(viagens)),dividir(receita,horas),dividir(receita,km),dividir(saldo,BigDecimal.valueOf(viagens)),dividir(saldo,horas),dividir(saldo,km));
        Map<UUID,BigDecimal> porOrigem=listaReceitas.stream().collect(Collectors.groupingBy(Receita::getOrigemId,Collectors.mapping(r->r.getValor().getValor(),Collectors.reducing(BigDecimal.ZERO,BigDecimal::add))));
        BigDecimal maior=porOrigem.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        List<OrigemDashboardResponse> resultadoOrigens=origens.findByTenantIdAndAtivoTrueOrderByNomeAsc(u.getTenantId()).stream().map(o->new OrigemDashboardResponse(o.getId(),o.getNome(),o.getImagemBase64(),porOrigem.getOrDefault(o.getId(),BigDecimal.ZERO),dividir(porOrigem.getOrDefault(o.getId(),BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)),maior))).toList();
        ResumoJornadaResponse ultima=jornadas.findFirstByStatusAndUsuarioIdOrderByDataDescHorarioInicioDesc(JornadaOperacional.StatusJornada.ENCERRADA,u.getId()).map(this::resumo).orElse(null);
        return new DashboardPeriodoResponse(inicio,fim,receita,despesa,saldo,estatisticas,resultadoOrigens,ultima);
    }
    private BigDecimal somarReceitas(List<Receita> receitas){return receitas.stream().map(r->r.getValor().getValor()).reduce(BigDecimal.ZERO,BigDecimal::add);}
    private BigDecimal dividir(BigDecimal a,BigDecimal b){return b==null||b.signum()==0?BigDecimal.ZERO:a.divide(b,4,RoundingMode.HALF_UP);}
    private ResumoJornadaResponse resumo(JornadaOperacional j){BigDecimal h=j.getHorarioFim()==null?BigDecimal.ZERO:BigDecimal.valueOf(Duration.between(j.getHorarioInicio(),j.getHorarioFim()).toMinutes()).divide(BigDecimal.valueOf(60),4,RoundingMode.HALF_UP);BigDecimal km=j.getOdometroFinal()==null?BigDecimal.ZERO:j.getOdometroFinal().subtract(j.getOdometroInicial());return new ResumoJornadaResponse(j.getData(),j.getHorarioInicio(),j.getHorarioFim(),h,km);}
}
