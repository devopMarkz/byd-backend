package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.VeiculoResponse;
import com.rideprofit.dominio.entidade.Veiculo;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVeiculosAtivos {

    private final VeiculoRepositorio veiculoRepositorio;

    public ListarVeiculosAtivos(VeiculoRepositorio veiculoRepositorio) {
        this.veiculoRepositorio = veiculoRepositorio;
    }

    public List<VeiculoResponse> executar() {
        UsuarioAutenticadoService.obterUsuarioAutenticado();
        return veiculoRepositorio.findByAtivoTrue().stream()
                .map(this::paraResponse)
                .toList();
    }

    private VeiculoResponse paraResponse(Veiculo veiculo) {
        return new VeiculoResponse(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getTipo(),
                veiculo.getCapacidadeBateriaKwh(),
                veiculo.getAutonomiaKm(),
                veiculo.getConsumoMedioKwhKm()
        );
    }
}
