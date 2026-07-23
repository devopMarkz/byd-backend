package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.DespesaRequest;
import com.rideprofit.aplicacao.dto.DespesaResponse;
import com.rideprofit.dominio.entidade.CategoriaSaida;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.FormaPagamentoRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RegistrarDespesa {

    private final DespesaRepositorio despesaRepositorio;
    private final CategoriaSaidaRepositorio categoriaSaidaRepositorio;
    private final FormaPagamentoRepositorio formaPagamentoRepositorio;

    public RegistrarDespesa(DespesaRepositorio despesaRepositorio,
                          CategoriaSaidaRepositorio categoriaSaidaRepositorio,
                          FormaPagamentoRepositorio formaPagamentoRepositorio) {
        this.despesaRepositorio = despesaRepositorio;
        this.categoriaSaidaRepositorio = categoriaSaidaRepositorio;
        this.formaPagamentoRepositorio = formaPagamentoRepositorio;
    }

    public DespesaResponse executar(DespesaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        CategoriaSaida categoria = categoriaSaidaRepositorio.findByIdAndTenantIdAndAtivoTrue(request.categoriaSaidaId(), usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria de saida nao encontrada."));
        var forma = request.formaPagamentoId() == null ? null : formaPagamentoRepositorio.findByIdAndTenantIdAndAtivoTrue(request.formaPagamentoId(), usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Forma de pagamento nao encontrada."));

        Despesa despesa = new Despesa();
        despesa.setTenantId(usuario.getTenantId());
        despesa.setUsuarioId(usuario.getId());
        despesa.setCategoriaSaidaId(categoria.getId());
        despesa.setFormaPagamentoId(forma != null ? forma.getId() : null);
        despesa.setValor(Dinheiro.emReais(request.valor()));
        despesa.setData(request.data());
        despesa.setJornadaId(request.jornadaId());
        despesa.setDiaSemana(request.diaSemana());
        despesa.setTipoGasto(request.tipoGasto());
        despesa.setDescricao(request.descricao());
        despesa.setItemManutencao(request.itemManutencao());
        definirNotaFiscal(despesa, request);
        despesa.setObservacao(request.observacao());

        Despesa salva = despesaRepositorio.save(despesa);

        return new DespesaResponse(
                salva.getId(),
                salva.getValor().getValor(),
                categoria.getId(),
                categoria.getNome(),
                salva.getFormaPagamentoId(),
                forma != null ? forma.getNome() : null,
                salva.getData(),
                salva.getJornadaId(),
                salva.getDiaSemana(),
                salva.getTipoGasto(),
                salva.getDescricao(),
                salva.getItemManutencao(),
                salva.getNotaFiscal() != null,
                salva.getNotaFiscalNome(),
                salva.getNotaFiscalTipo(),
                salva.getObservacao()
        );
    }

    static void definirNotaFiscal(Despesa despesa, DespesaRequest request) {
        if (request.notaFiscalBase64() == null || request.notaFiscalBase64().isBlank()) {
            despesa.setNotaFiscal(null);
            despesa.setNotaFiscalNome(null);
            despesa.setNotaFiscalTipo(null);
            return;
        }
        if (request.notaFiscalTipo() == null || !java.util.Set.of("application/pdf", "image/jpeg", "image/png").contains(request.notaFiscalTipo())) {
            throw new IllegalArgumentException("Nota fiscal deve ser PDF, JPG ou PNG.");
        }
        try {
            String conteudo = request.notaFiscalBase64().contains(",") ? request.notaFiscalBase64().substring(request.notaFiscalBase64().indexOf(',') + 1) : request.notaFiscalBase64();
            despesa.setNotaFiscal(java.util.Base64.getDecoder().decode(conteudo));
            despesa.setNotaFiscalNome(request.notaFiscalNome());
            despesa.setNotaFiscalTipo(request.notaFiscalTipo());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Conteudo da nota fiscal invalido.");
        }
    }
}
