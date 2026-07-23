package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.VeiculoRequest;
import com.rideprofit.aplicacao.dto.VeiculoResponse;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.entidade.Veiculo;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

@Service
public class CadastrarVeiculo {

    private final VeiculoRepositorio veiculoRepositorio;

    public CadastrarVeiculo(VeiculoRepositorio veiculoRepositorio) {
        this.veiculoRepositorio = veiculoRepositorio;
    }

    public VeiculoResponse executar(VeiculoRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        Veiculo veiculo = new Veiculo();
        veiculo.setTenantId(usuario.getTenantId());
        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setAno(request.ano());
        veiculo.setTipo(request.tipo());
        veiculo.setCapacidadeBateriaKwh(request.capacidadeBateriaKwh());
        veiculo.setAutonomiaKm(request.autonomiaKm());
        veiculo.setConsumoMedioKwhKm(request.consumoMedioKwhKm());

        Veiculo salvo = veiculoRepositorio.save(veiculo);
        return paraResponse(salvo);
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
