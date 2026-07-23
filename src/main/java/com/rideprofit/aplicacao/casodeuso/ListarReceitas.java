package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.ReceitaResponse;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.entidade.Receita;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.persistencia.OrigemRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListarReceitas {

    private final ReceitaRepositorio receitaRepositorio;
    private final OrigemRepositorio origemRepositorio;

    public ListarReceitas(ReceitaRepositorio receitaRepositorio, OrigemRepositorio origemRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
        this.origemRepositorio = origemRepositorio;
    }

    public List<ReceitaResponse> executar(LocalDate inicio, LocalDate fim) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<Receita> receitas = receitaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);
        var origens = origemRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(usuario.getTenantId()).stream().collect(java.util.stream.Collectors.toMap(com.rideprofit.dominio.entidade.Origem::getId, com.rideprofit.dominio.entidade.Origem::getNome));
        return receitas.stream()
                .map(r -> new ReceitaResponse(
                        r.getId(),
                        r.getValor().getValor(),
                        r.getData(),
                        r.getHorario(),
                        r.getDataHoraInicio(),
                        r.getDataHoraFim(),
                        r.getOrigemId(),
                        origens.getOrDefault(r.getOrigemId(), "Desconhecida"),
                        r.getJornadaId(),
                        r.getPlataforma(),
                        r.getDiaSemana(),
                        r.getQuantidadeViagens(),
                        r.getQuilometrosRodados(),
                        r.getHorasTrabalhadas(),
                        r.getObservacao(),
                        r.getNotaFiscalNome(),
                        r.getNotaFiscalTipo(),
                        r.getNotaFiscal() != null
                ))
                .toList();
    }
}
