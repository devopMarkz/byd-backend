package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.FormaPagamentoRequest;
import com.rideprofit.aplicacao.dto.FormaPagamentoResponse;
import com.rideprofit.dominio.entidade.FormaPagamento;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.FormaPagamentoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GerenciarFormasPagamento {
    private final FormaPagamentoRepositorio formas;
    private final DespesaRepositorio despesas;

    public GerenciarFormasPagamento(FormaPagamentoRepositorio formas, DespesaRepositorio despesas) {
        this.formas = formas;
        this.despesas = despesas;
    }

    public List<FormaPagamentoResponse> listar() {
        Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado();
        return formas.findByTenantIdAndAtivoTrueOrderByNomeAsc(u.getTenantId()).stream().map(this::resposta).toList();
    }

    public FormaPagamentoResponse criar(FormaPagamentoRequest r) {
        Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado();
        FormaPagamento f = new FormaPagamento();
        f.setTenantId(u.getTenantId());
        f.setNome(r.nome().trim());
        return resposta(formas.save(f));
    }

    public FormaPagamentoResponse editar(UUID id, FormaPagamentoRequest r) {
        FormaPagamento f = obter(id);
        f.setNome(r.nome().trim());
        return resposta(formas.save(f));
    }

    public void excluir(UUID id) {
        Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado();

        FormaPagamento f = obter(id);

        if (despesas.existsByTenantIdAndFormaPagamentoIdAndAtivoTrue(
                u.getTenantId(),
                id
        )) {
            throw new IllegalStateException(
                    "A forma de pagamento possui despesas vinculadas e nao pode ser excluida."
            );
        }

        formas.delete(f);
    }

    private FormaPagamento obter(UUID id) {
        Usuario u = UsuarioAutenticadoService.obterUsuarioAutenticado();
        return formas.findByIdAndTenantIdAndAtivoTrue(id, u.getTenantId()).orElseThrow(() -> new EntityNotFoundException("Forma de pagamento nao encontrada."));
    }

    private FormaPagamentoResponse resposta(FormaPagamento f) {
        return new FormaPagamentoResponse(f.getId(), f.getNome());
    }
}
