package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.JornadaOperacional;
import com.rideprofit.infraestrutura.persistencia.JornadaOperacionalRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirJornada {

    private final JornadaOperacionalRepositorio jornadaRepositorio;

    public ExcluirJornada(JornadaOperacionalRepositorio jornadaRepositorio) {
        this.jornadaRepositorio = jornadaRepositorio;
    }

    @Transactional
    public void executar(UUID id) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        JornadaOperacional jornada = jornadaRepositorio
                .findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Jornada nao encontrada."
                ));

        jornada.setAtivo(false);

        jornadaRepositorio.save(jornada);
    }
}