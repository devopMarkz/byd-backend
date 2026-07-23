package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DespesaResponse;
import com.rideprofit.dominio.entidade.CategoriaSaida;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.FormaPagamentoRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListarDespesas {

    private final DespesaRepositorio despesaRepositorio;
    private final CategoriaSaidaRepositorio categoriaSaidaRepositorio;
    private final FormaPagamentoRepositorio formaPagamentoRepositorio;

    public ListarDespesas(DespesaRepositorio despesaRepositorio,
                          CategoriaSaidaRepositorio categoriaSaidaRepositorio,
                          FormaPagamentoRepositorio formaPagamentoRepositorio) {
        this.despesaRepositorio = despesaRepositorio;
        this.categoriaSaidaRepositorio = categoriaSaidaRepositorio;
        this.formaPagamentoRepositorio = formaPagamentoRepositorio;
    }

    public List<DespesaResponse> executar(LocalDate inicio, LocalDate fim) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<Despesa> despesas = despesaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);

        Map<UUID, String> nomesCategorias = categoriaSaidaRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId()).stream()
                .collect(Collectors.toMap(CategoriaSaida::getId, CategoriaSaida::getNome));
        Map<UUID, String> nomesFormas = formaPagamentoRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId()).stream()
                .collect(Collectors.toMap(com.rideprofit.dominio.entidade.FormaPagamento::getId, com.rideprofit.dominio.entidade.FormaPagamento::getNome));

        return despesas.stream()
                .map(d -> new DespesaResponse(
                        d.getId(),
                        d.getValor().getValor(),
                        d.getCategoriaSaidaId(),
                        nomesCategorias.getOrDefault(d.getCategoriaSaidaId(), "Desconhecida"),
                        d.getFormaPagamentoId(),
                        nomesFormas.get(d.getFormaPagamentoId()),
                        d.getData(),
                        d.getJornadaId(),
                        d.getDiaSemana(),
                        d.getTipoGasto(),
                        d.getDescricao(),
                        d.getItemManutencao(),
                        d.getNotaFiscal() != null,
                        d.getNotaFiscalNome(),
                        d.getNotaFiscalTipo(),
                        d.getObservacao()
                ))
                .toList();
    }
}
