package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.EncerrarJornadaRequest;
import com.rideprofit.aplicacao.dto.JornadaResponse;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.dominio.valor.PercentualBateria;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EncerrarJornada {

    private final JornadaOperacionalRepositorio jornadaRepositorio;

    public EncerrarJornada(JornadaOperacionalRepositorio jornadaRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio;
    }

    public JornadaResponse executar(EncerrarJornadaRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        JornadaOperacional jornada = jornadaRepositorio.findByIdAndTenantIdAndAtivoTrue(request.jornadaId(), usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Jornada nao encontrada."));

        if (jornada.getStatus() == JornadaOperacional.StatusJornada.ENCERRADA) {
            throw new IllegalStateException("Jornada ja encerrada.");
        }

        jornada.setHorarioFim(request.horarioFim());
        jornada.setOdometroFinal(request.odometroFinal());
        jornada.setPercentualBateriaFinal(PercentualBateria.de(request.percentualBateriaFinal()));
        jornada.setStatus(JornadaOperacional.StatusJornada.ENCERRADA);

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
