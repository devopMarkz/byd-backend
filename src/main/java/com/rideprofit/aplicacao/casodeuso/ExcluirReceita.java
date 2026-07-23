package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.Receita;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirReceita {

    private final ReceitaRepositorio receitaRepositorio;

    public ExcluirReceita(ReceitaRepositorio receitaRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
    }

    public void executar(UUID id) {
        UsuarioAutenticadoService.obterUsuarioAutenticado();
        Receita receita = receitaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita nao encontrada."));
        receita.setAtivo(false);
        receitaRepositorio.save(receita);
    }
}
