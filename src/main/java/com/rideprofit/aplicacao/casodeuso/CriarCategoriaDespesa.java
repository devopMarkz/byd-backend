package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.CategoriaDespesaRequest;
import com.rideprofit.aplicacao.dto.CategoriaDespesaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

@Service
public class CriarCategoriaDespesa {

    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public CriarCategoriaDespesa(CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public CategoriaDespesaResponse executar(CategoriaDespesaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        CategoriaDespesa categoria = new CategoriaDespesa();
        categoria.setTenantId(usuario.getTenantId());
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setPadrao(false);

        CategoriaDespesa salva = categoriaDespesaRepositorio.save(categoria);
        return paraResponse(salva);
    }

    private CategoriaDespesaResponse paraResponse(CategoriaDespesa categoria) {
        return new CategoriaDespesaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isPadrao()
        );
    }
}
