package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.CategoriaDespesaRequest;
import com.rideprofit.aplicacao.dto.CategoriaDespesaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EditarCategoriaDespesa {

    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public EditarCategoriaDespesa(CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public CategoriaDespesaResponse executar(UUID id, CategoriaDespesaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        CategoriaDespesa categoria = categoriaDespesaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria de despesa nao encontrada."));

        if (!categoria.getTenantId().equals(usuario.getTenantId())) {
            throw new EntityNotFoundException("Categoria de despesa nao encontrada.");
        }

        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

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
