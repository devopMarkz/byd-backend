package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DespesaRequest;
import com.rideprofit.aplicacao.dto.DespesaResponse;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.FormaPagamentoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarDespesa {
    private final DespesaRepositorio despesaRepositorio;
    private final CategoriaSaidaRepositorio categoriaSaidaRepositorio;
    private final FormaPagamentoRepositorio formaPagamentoRepositorio;

    public AtualizarDespesa(DespesaRepositorio despesaRepositorio, CategoriaSaidaRepositorio categoriaSaidaRepositorio, FormaPagamentoRepositorio formaPagamentoRepositorio) {
        this.despesaRepositorio = despesaRepositorio;
        this.categoriaSaidaRepositorio = categoriaSaidaRepositorio;
        this.formaPagamentoRepositorio = formaPagamentoRepositorio;
    }

    public DespesaResponse executar(UUID id, DespesaRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        var despesa = despesaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId()).orElseThrow(() -> new EntityNotFoundException("Saida nao encontrada."));
        var categoria = categoriaSaidaRepositorio.findByIdAndTenantIdAndAtivoTrue(request.categoriaSaidaId(), usuario.getTenantId()).orElseThrow(() -> new EntityNotFoundException("Categoria de saida nao encontrada."));
        var forma = request.formaPagamentoId() == null ? null : formaPagamentoRepositorio.findByIdAndTenantIdAndAtivoTrue(request.formaPagamentoId(), usuario.getTenantId()).orElseThrow(() -> new EntityNotFoundException("Forma de pagamento nao encontrada."));
        despesa.setValor(Dinheiro.emReais(request.valor()));
        despesa.setCategoriaSaidaId(categoria.getId());
        despesa.setFormaPagamentoId(forma == null ? null : forma.getId());
        despesa.setData(request.data());
        despesa.setJornadaId(request.jornadaId());
        despesa.setDiaSemana(request.diaSemana());
        despesa.setTipoGasto(request.tipoGasto());
        despesa.setDescricao(request.descricao());
        despesa.setItemManutencao(request.itemManutencao());
        RegistrarDespesa.definirNotaFiscal(despesa, request);
        despesa.setObservacao(request.observacao());
        var salva = despesaRepositorio.save(despesa);
        return new DespesaResponse(salva.getId(), salva.getValor().getValor(), categoria.getId(), categoria.getNome(), salva.getFormaPagamentoId(), forma == null ? null : forma.getNome(), salva.getData(), salva.getJornadaId(), salva.getDiaSemana(), salva.getTipoGasto(), salva.getDescricao(), salva.getItemManutencao(), salva.getNotaFiscal() != null, salva.getNotaFiscalNome(), salva.getNotaFiscalTipo(), salva.getObservacao());
    }
}
