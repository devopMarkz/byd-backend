package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.RecargaResponse;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListarRecargas {

    private final RecargaRepositorio recargaRepositorio;

    public ListarRecargas(RecargaRepositorio recargaRepositorio) {
        this.recargaRepositorio = recargaRepositorio;
    }

    public List<RecargaResponse> executar(LocalDate inicio, LocalDate fim) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        List<Recarga> recargas = recargaRepositorio.findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(usuario.getTenantId(), inicio, fim);
        return recargas.stream()
                .map(this::paraResponse)
                .toList();
    }

    private RecargaResponse paraResponse(Recarga recarga) {
        return new RecargaResponse(
                recarga.getId(),
                recarga.getVeiculoId(),
                recarga.getDespesaId(),
                recarga.getData(),
                recarga.getHorario(),
                recarga.getEnergiaConsumida().getKWh(),
                recarga.getTarifaKwh(),
                recarga.getCusto().getValor(),
                recarga.getLocalRecarga(),
                recarga.getObservacao(),
                recarga.getNotaFiscalNome(),
                recarga.getNotaFiscalTipo(),
                recarga.getNotaFiscal() != null
        );
    }
}
