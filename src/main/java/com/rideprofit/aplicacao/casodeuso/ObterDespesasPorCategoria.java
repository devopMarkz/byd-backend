package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DespesaPorCategoriaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ObterDespesasPorCategoria {

    private final DespesaRepositorio despesaRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public ObterDespesasPorCategoria(DespesaRepositorio despesaRepositorio,
                                      CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.despesaRepositorio = despesaRepositorio;
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public List<DespesaPorCategoriaResponse> executar(LocalDate inicio, LocalDate fim) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        List<Despesa> despesas = despesaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);

        Map<UUID, String> nomesCategorias = categoriaDespesaRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId()).stream()
                .collect(Collectors.toMap(CategoriaDespesa::getId, CategoriaDespesa::getNome));

        Map<String, BigDecimal> totalPorCategoria = despesas.stream()
                .collect(Collectors.groupingBy(
                        d -> nomesCategorias.getOrDefault(d.getCategoriaDespesaId(), "Sem categoria"),
                        Collectors.mapping(d -> d.getValor().getValor(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        BigDecimal totalGeral = totalPorCategoria.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPorCategoria.entrySet().stream()
                .map(entry -> new DespesaPorCategoriaResponse(
                        entry.getKey(),
                        entry.getValue(),
                        calcularPercentual(entry.getValue(), totalGeral)
                ))
                .sorted((a, b) -> b.total().compareTo(a.total()))
                .collect(Collectors.toList());
    }

    private Double calcularPercentual(BigDecimal valor, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return valor.multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
