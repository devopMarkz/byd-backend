package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirRecarga {

    private final RecargaRepositorio recargaRepositorio;
    private final DespesaRepositorio despesaRepositorio;

    public ExcluirRecarga(RecargaRepositorio recargaRepositorio, DespesaRepositorio despesaRepositorio) {
        this.recargaRepositorio = recargaRepositorio;
        this.despesaRepositorio = despesaRepositorio;
    }

    @Transactional
    public void executar(UUID id) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        Recarga recarga = recargaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Recarga nao encontrada."));
        recarga.setAtivo(false);
        recargaRepositorio.save(recarga);

        if (recarga.getDespesaId() != null) {
            despesaRepositorio.findByIdAndTenantIdAndAtivoTrue(recarga.getDespesaId(), usuario.getTenantId())
                    .ifPresent(despesa -> {
                        despesa.setAtivo(false);
                        despesaRepositorio.save(despesa);
                    });
        }
    }
}
