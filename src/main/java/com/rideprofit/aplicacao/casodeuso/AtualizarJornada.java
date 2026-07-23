package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.AtualizarJornadaRequest;
import com.rideprofit.aplicacao.dto.JornadaResponse;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AtualizarJornada {

    private final JornadaOperacionalRepositorio jornadaRepositorio;

    public AtualizarJornada(JornadaOperacionalRepositorio jornadaRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio;
    }

    @Transactional
    public JornadaResponse executar(UUID id, AtualizarJornadaRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        JornadaOperacional jornada = jornadaRepositorio
                .findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Jornada nao encontrada."));

        validarHorarios(request);

        jornada.setData(request.data());
        jornada.setHorarioInicio(request.horarioInicio());
        jornada.setHorarioFim(request.horarioFim());

        JornadaOperacional salva = jornadaRepositorio.save(jornada);

        return paraResponse(salva);
    }

    private void validarHorarios(AtualizarJornadaRequest request) {
        if (request.horarioFim().isBefore(request.horarioInicio())) {
            throw new IllegalArgumentException(
                    "O horario de fim nao pode ser anterior ao horario de inicio."
            );
        }
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
                jornada.getPercentualBateriaInicial() == null
                        ? null
                        : jornada.getPercentualBateriaInicial().getValor(),
                jornada.getPercentualBateriaFinal() == null
                        ? null
                        : jornada.getPercentualBateriaFinal().getValor(),
                jornada.getStatus().name()
        );
    }
}