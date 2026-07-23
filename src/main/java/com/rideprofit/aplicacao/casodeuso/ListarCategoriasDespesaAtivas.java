package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.CategoriaDespesaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCategoriasDespesaAtivas {

    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public ListarCategoriasDespesaAtivas(CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public List<CategoriaDespesaResponse> executar() {
        UsuarioAutenticadoService.obterUsuarioAutenticado();
        return categoriaDespesaRepositorio.findByAtivoTrueOrderByNomeAsc().stream()
                .map(this::paraResponse)
                .toList();
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
