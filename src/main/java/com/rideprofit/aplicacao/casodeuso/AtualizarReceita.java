package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.ReceitaRequest;
import com.rideprofit.aplicacao.dto.ReceitaResponse;
import com.rideprofit.dominio.entidade.Receita;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.infraestrutura.persistencia.OrigemRepositorio;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarReceita {
    private final ReceitaRepositorio receitaRepositorio;
    private final OrigemRepositorio origemRepositorio;

    public AtualizarReceita(ReceitaRepositorio receitaRepositorio, OrigemRepositorio origemRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
        this.origemRepositorio = origemRepositorio;
    }

    public ReceitaResponse executar(UUID id, ReceitaRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        Receita receita = receitaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Entrada nao encontrada."));
        var origem = origemRepositorio.findByIdAndTenantIdAndAtivoTrue(request.origemId(), usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Origem nao encontrada."));
        receita.setValor(Dinheiro.emReais(request.valor()));
        receita.setData(request.data());
        receita.setHorario(request.horario());
        receita.setDataHoraInicio(request.dataHoraInicio());
        receita.setDataHoraFim(request.dataHoraFim());
        receita.setOrigemId(origem.getId());
        receita.setJornadaId(request.jornadaId());
        receita.setPlataforma(request.plataforma());
        receita.setDiaSemana(request.diaSemana());
        receita.setQuantidadeViagens(request.quantidadeViagens());
        receita.setQuilometrosRodados(request.quilometrosRodados());
        receita.setHorasTrabalhadas(request.horasTrabalhadas());
        receita.setObservacao(request.observacao());
        if (request.notaFiscalBase64() != null && !request.notaFiscalBase64().isBlank()) {
            String conteudo = request.notaFiscalBase64().contains(",") ? request.notaFiscalBase64().substring(request.notaFiscalBase64().indexOf(',') + 1) : request.notaFiscalBase64();
            receita.setNotaFiscal(java.util.Base64.getDecoder().decode(conteudo));
            receita.setNotaFiscalNome(request.notaFiscalNome());
            receita.setNotaFiscalTipo(request.notaFiscalTipo());
        }
        Receita salva = receitaRepositorio.save(receita);
        return new ReceitaResponse(salva.getId(), salva.getValor().getValor(), salva.getData(), salva.getHorario(), salva.getDataHoraInicio(), salva.getDataHoraFim(), salva.getOrigemId(), origem.getNome(), salva.getJornadaId(), salva.getPlataforma(), salva.getDiaSemana(), salva.getQuantidadeViagens(), salva.getQuilometrosRodados(), salva.getHorasTrabalhadas(), salva.getObservacao(), salva.getNotaFiscalNome(), salva.getNotaFiscalTipo(), salva.getNotaFiscal() != null);
    }
}
