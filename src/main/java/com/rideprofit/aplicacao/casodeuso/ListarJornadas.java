package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.JornadaResponse;
import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListarJornadas {

    private final JornadaOperacionalRepositorio jornadaRepositorio;

    public ListarJornadas(JornadaOperacionalRepositorio jornadaRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio;
    }

    public List<JornadaResponse> executar(LocalDate inicio, LocalDate fim) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<JornadaOperacional> jornadas = jornadaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(usuario.getTenantId(), inicio, fim);
        return jornadas.stream()
                .map(this::paraResponse)
                .toList();
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
