package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.RecargaRequest;
import com.rideprofit.aplicacao.dto.RecargaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.dominio.valor.EnergiaConsumida;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
public class AtualizarRecarga {

    private final RecargaRepositorio recargaRepositorio;
    private final DespesaRepositorio despesaRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public AtualizarRecarga(
            RecargaRepositorio recargaRepositorio,
            DespesaRepositorio despesaRepositorio,
            CategoriaDespesaRepositorio categoriaDespesaRepositorio) {

        this.recargaRepositorio = recargaRepositorio;
        this.despesaRepositorio = despesaRepositorio;
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    @Transactional
    public RecargaResponse executar(UUID id, RecargaRequest request) {

        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        Recarga recarga = recargaRepositorio
                .findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Recarga nao encontrada."));

        BigDecimal tarifaKwh = request.valor()
                .divide(request.kwhConsumidos(), 4, RoundingMode.HALF_UP);

        recarga.setData(request.data());

        recarga.setEnergiaConsumida(
                EnergiaConsumida.emKwh(request.kwhConsumidos())
        );

        recarga.setTarifaKwh(tarifaKwh);

        recarga.setCusto(
                Dinheiro.emReais(request.valor())
        );

        recarga.setLocalRecarga(request.localRecarga());
        recarga.setObservacao(request.observacao());

        atualizarNotaFiscal(recarga, request);

        atualizarDespesaVinculada(
                recarga,
                request,
                usuario.getTenantId()
        );

        Recarga salva = recargaRepositorio.save(recarga);

        return paraResponse(salva);
    }

    private void atualizarDespesaVinculada(
            Recarga recarga,
            RecargaRequest request,
            UUID tenantId) {

        if (recarga.getDespesaId() == null) {
            return;
        }

        Despesa despesa = despesaRepositorio
                .findByIdAndTenantIdAndAtivoTrue(
                        recarga.getDespesaId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Despesa vinculada a recarga nao encontrada."
                        ));

        despesa.setValor(
                Dinheiro.emReais(request.valor())
        );

        despesa.setData(request.data());

        despesa.setObservacao(
                request.observacao()
        );

        despesa.setDescricao(
                "Recarga em " +
                        (request.localRecarga() != null
                                ? request.localRecarga()
                                : "local nao informado")
        );

        atualizarNotaFiscalDaDespesa(despesa, request);

        despesaRepositorio.save(despesa);
    }

    private void atualizarNotaFiscal(
            Recarga recarga,
            RecargaRequest request) {

        if (request.notaFiscalBase64() == null
                || request.notaFiscalBase64().isBlank()) {

            recarga.setNotaFiscal(null);
            recarga.setNotaFiscalNome(null);
            recarga.setNotaFiscalTipo(null);

            return;
        }

        validarTipoNotaFiscal(request.notaFiscalTipo());

        String conteudo = extrairConteudoBase64(
                request.notaFiscalBase64()
        );

        recarga.setNotaFiscal(
                Base64.getDecoder().decode(conteudo)
        );

        recarga.setNotaFiscalNome(
                request.notaFiscalNome()
        );

        recarga.setNotaFiscalTipo(
                request.notaFiscalTipo()
        );
    }

    private void atualizarNotaFiscalDaDespesa(
            Despesa despesa,
            RecargaRequest request) {

        if (request.notaFiscalBase64() == null
                || request.notaFiscalBase64().isBlank()) {

            despesa.setNotaFiscal(null);
            despesa.setNotaFiscalNome(null);
            despesa.setNotaFiscalTipo(null);

            return;
        }

        validarTipoNotaFiscal(request.notaFiscalTipo());

        String conteudo = extrairConteudoBase64(
                request.notaFiscalBase64()
        );

        despesa.setNotaFiscal(
                Base64.getDecoder().decode(conteudo)
        );

        despesa.setNotaFiscalNome(
                request.notaFiscalNome()
        );

        despesa.setNotaFiscalTipo(
                request.notaFiscalTipo()
        );
    }

    private void validarTipoNotaFiscal(String tipo) {

        if (tipo == null
                || !Set.of(
                "application/pdf",
                "image/jpeg",
                "image/png"
        ).contains(tipo)) {

            throw new IllegalArgumentException(
                    "Nota fiscal deve ser PDF, JPG ou PNG."
            );
        }
    }

    private String extrairConteudoBase64(String base64) {

        return base64.contains(",")
                ? base64.substring(
                base64.indexOf(',') + 1
        )
                : base64;
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