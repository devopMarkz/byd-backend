package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.IniciarJornadaRequest;
import com.rideprofit.aplicacao.dto.JornadaResponse;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.valor.PercentualBateria;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IniciarJornada {

    private final JornadaOperacionalRepositorio jornadaRepositorio;
    private final VeiculoRepositorio veiculoRepositorio;

    public IniciarJornada(JornadaOperacionalRepositorio jornadaRepositorio,
                          VeiculoRepositorio veiculoRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio;
        this.veiculoRepositorio = veiculoRepositorio;
    }

    public JornadaResponse executar(IniciarJornadaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        veiculoRepositorio.findByIdAndTenantIdAndAtivoTrue(request.veiculoId(), usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Veiculo nao encontrado."));

        JornadaOperacional jornada = new JornadaOperacional();
        jornada.setTenantId(usuario.getTenantId());
        jornada.setUsuarioId(usuario.getId());
        jornada.setVeiculoId(request.veiculoId());
        jornada.setData(request.data());
        jornada.setHorarioInicio(request.horarioInicio());
        jornada.setOdometroInicial(request.odometroInicial());
        jornada.setPercentualBateriaInicial(PercentualBateria.de(request.percentualBateriaInicial()));
        jornada.setStatus(JornadaOperacional.StatusJornada.EM_ANDAMENTO);

        JornadaOperacional salva = jornadaRepositorio.save(jornada);
        return paraResponse(salva);
    }

    private JornadaResponse paraResponse(JornadaOperacional jornada) {
        return new JornadaResponse(
                jornada.getId(),
                jornada.getVeiculoId(),
                jornada.getData(),
                jornada.getHorarioInicio(),
                jornada.getHorarioFim(),
                jornada.getOdometroInicial(),
                jornada.getOdometroFinal(),
                jornada.getPercentualBateriaInicial().getValor(),
                jornada.getPercentualBateriaFinal() != null ? jornada.getPercentualBateriaFinal().getValor() : null,
                jornada.getStatus().name()
        );
    }
}
