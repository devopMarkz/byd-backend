package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DesativarCategoriaDespesa {

    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public DesativarCategoriaDespesa(CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    public void executar(UUID id) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        CategoriaDespesa categoria = categoriaDespesaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria de despesa nao encontrada."));

        if (!categoria.getTenantId().equals(usuario.getTenantId())) {
            throw new EntityNotFoundException("Categoria de despesa nao encontrada.");
        }

        categoria.setAtivo(false);
        categoriaDespesaRepositorio.save(categoria);
    }
}
