package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DashboardResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.dominio.entidade.Receita;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.dominio.valor.EnergiaConsumida;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalcularDashboardFinanceiroTest {

    private final ReceitaRepositorio receitaRepositorio = mock(ReceitaRepositorio.class);
    private final DespesaRepositorio despesaRepositorio = mock(DespesaRepositorio.class);
    private final RecargaRepositorio recargaRepositorio = mock(RecargaRepositorio.class);
    private final JornadaOperacionalRepositorio jornadaRepositorio = mock(JornadaOperacionalRepositorio.class);
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio = mock(CategoriaDespesaRepositorio.class);

    private final CalcularDashboardFinanceiro useCase = new CalcularDashboardFinanceiro(
            receitaRepositorio, despesaRepositorio, recargaRepositorio, jornadaRepositorio, categoriaDespesaRepositorio
    );

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveCalcularIndicadoresFinanceirosCorretamente() {
        Usuario usuario = new Usuario();
        usuario.setId(java.util.UUID.randomUUID());
        usuario.setTenantId(java.util.UUID.randomUUID());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(usuario, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LocalDate data = LocalDate.of(2026, 7, 17);

        CategoriaDespesa categoriaEnergia = new CategoriaDespesa();
        categoriaEnergia.setId(java.util.UUID.randomUUID());
        categoriaEnergia.setNome("Energia eletrica");
        when(categoriaDespesaRepositorio.findByAtivoTrueOrderByNomeAsc()).thenReturn(List.of(categoriaEnergia));

        Receita receita = new Receita();
        receita.setValor(Dinheiro.emReais(new BigDecimal("200.00")));
        receita.setQuantidadeViagens(12);
        receita.setQuilometrosRodados(new BigDecimal("200.0"));
        receita.setHorasTrabalhadas(new BigDecimal("10.00"));
        when(receitaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(any(), any(), any())).thenReturn(List.of(receita));

        Despesa despesa = new Despesa();
        despesa.setValor(Dinheiro.emReais(new BigDecimal("30.00")));
        when(despesaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(any(), any(), any())).thenReturn(List.of(despesa));

        Recarga recarga = new Recarga();
        recarga.setCusto(Dinheiro.emReais(new BigDecimal("20.00")));
        recarga.setEnergiaConsumida(EnergiaConsumida.emKwh(new BigDecimal("25.00")));
        when(recargaRepositorio.findByDataBetweenAndAtivoTrueOrderByDataDesc(any(), any())).thenReturn(List.of(recarga));

        JornadaOperacional jornada = new JornadaOperacional();
        jornada.setOdometroInicial(new BigDecimal("10000.00"));
        jornada.setOdometroFinal(new BigDecimal("10150.00"));
        jornada.setHorarioInicio(LocalTime.of(8, 0));
        jornada.setHorarioFim(LocalTime.of(13, 0));
        when(jornadaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(any(), any(), any())).thenReturn(List.of(jornada));

        DashboardResponse response = useCase.executar(data);

        assertThat(response.faturamento()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(response.despesas()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(response.custoEnergetico()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(response.lucroLiquido()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(response.quilometrosPercorridos()).isEqualByComparingTo(new BigDecimal("200.0"));
        assertThat(response.horasTrabalhadas()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(response.totalViagens()).isEqualTo(12);
        assertThat(response.faturamentoMedioPorHora()).isEqualByComparingTo(new BigDecimal("20.0000"));
        assertThat(response.faturamentoMedioPorKm()).isEqualByComparingTo(new BigDecimal("1.0000"));
        assertThat(response.custoPorViagem()).isEqualByComparingTo(new BigDecimal("1.6667"));
        assertThat(response.kWhConsumidos()).isEqualByComparingTo(new BigDecimal("25.00"));
    }
}
