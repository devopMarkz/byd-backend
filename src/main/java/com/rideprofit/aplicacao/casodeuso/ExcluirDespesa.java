package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirDespesa {

    private final DespesaRepositorio despesaRepositorio;

    public ExcluirDespesa(DespesaRepositorio despesaRepositorio) {
        this.despesaRepositorio = despesaRepositorio;
    }

    public void executar(UUID id) {
        UsuarioAutenticadoService.obterUsuarioAutenticado();
        Despesa despesa = despesaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa nao encontrada."));
        despesa.setAtivo(false);
        despesaRepositorio.save(despesa);
    }
}
