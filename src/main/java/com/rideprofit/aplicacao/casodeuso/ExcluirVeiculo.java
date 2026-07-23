package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.Veiculo;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirVeiculo {

    private final VeiculoRepositorio veiculoRepositorio;

    public ExcluirVeiculo(VeiculoRepositorio veiculoRepositorio) {
        this.veiculoRepositorio = veiculoRepositorio;
    }

    public void executar(UUID id) {
        UsuarioAutenticadoService.obterUsuarioAutenticado();
        Veiculo veiculo = veiculoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo nao encontrado."));
        veiculo.setAtivo(false);
        veiculoRepositorio.save(veiculo);
    }
}
