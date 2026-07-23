package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.RecargaRequest;
import com.rideprofit.aplicacao.dto.RecargaResponse;
import com.rideprofit.dominio.entidade.CategoriaDespesa;
import com.rideprofit.dominio.entidade.CategoriaSaida;
import com.rideprofit.dominio.entidade.Despesa;
import com.rideprofit.dominio.entidade.Recarga;
import com.rideprofit.dominio.entidade.Tenant;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.dominio.entidade.Veiculo;
import com.rideprofit.dominio.valor.Dinheiro;
import com.rideprofit.dominio.valor.EnergiaConsumida;
import com.rideprofit.infraestrutura.persistencia.CategoriaDespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.CategoriaSaidaRepositorio;
import com.rideprofit.infraestrutura.persistencia.DespesaRepositorio;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import com.rideprofit.infraestrutura.persistencia.TenantRepositorio;
import com.rideprofit.infraestrutura.persistencia.VeiculoRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class RegistrarRecarga {

    private static final String CATEGORIA_RECARGA = "Recarga";

    private final RecargaRepositorio recargaRepositorio;
    private final DespesaRepositorio despesaRepositorio;
    private final TenantRepositorio tenantRepositorio;
    private final VeiculoRepositorio veiculoRepositorio;
    private final CategoriaSaidaRepositorio categoriaSaidaRepositorio;
    private final CategoriaDespesaRepositorio categoriaDespesaRepositorio;

    public RegistrarRecarga(RecargaRepositorio recargaRepositorio,
                            DespesaRepositorio despesaRepositorio,
                            TenantRepositorio tenantRepositorio,
                            VeiculoRepositorio veiculoRepositorio,
                            CategoriaSaidaRepositorio categoriaSaidaRepositorio,
                            CategoriaDespesaRepositorio categoriaDespesaRepositorio) {
        this.recargaRepositorio = recargaRepositorio;
        this.despesaRepositorio = despesaRepositorio;
        this.tenantRepositorio = tenantRepositorio;
        this.veiculoRepositorio = veiculoRepositorio;
        this.categoriaSaidaRepositorio = categoriaSaidaRepositorio;
        this.categoriaDespesaRepositorio = categoriaDespesaRepositorio;
    }

    @Transactional
    public RecargaResponse executar(RecargaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        Veiculo veiculo = veiculoRepositorio.findByTenantIdAndAtivoTrue(usuario.getTenantId()).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Veiculo padrao nao encontrado."));

        BigDecimal tarifaKwh = request.valor()
                .divide(request.kwhConsumidos(), 4, RoundingMode.HALF_UP);

        Recarga recarga = new Recarga();
        recarga.setTenantId(usuario.getTenantId());
        recarga.setUsuarioId(usuario.getId());
        recarga.setVeiculoId(veiculo.getId());
        recarga.setData(request.data());
        recarga.setHorario(LocalTime.now());
        recarga.setPercentualInicial(BigDecimal.ZERO);
        recarga.setPercentualFinal(BigDecimal.ZERO);
        recarga.setEnergiaConsumida(EnergiaConsumida.emKwh(request.kwhConsumidos()));
        recarga.setTarifaKwh(tarifaKwh);
        recarga.setCusto(Dinheiro.emReais(request.valor()));
        recarga.setLocalRecarga(request.localRecarga());
        recarga.setObservacao(request.observacao());
        aplicarNotaFiscal(recarga, request);

        Despesa despesa = criarDespesaRecarga(usuario, request);
        Despesa despesaSalva = despesaRepositorio.save(despesa);

        recarga.setDespesaId(despesaSalva.getId());
        Recarga salva = recargaRepositorio.save(recarga);

        return paraResponse(salva);
    }

    private Despesa criarDespesaRecarga(Usuario usuario, RecargaRequest request) {
        CategoriaSaida categoriaSaida = obterOuCriarCategoriaRecarga(usuario);
        CategoriaDespesa categoriaEnergia = obterCategoriaEnergia(usuario.getTenantId());

        Despesa despesa = new Despesa();
        despesa.setTenantId(usuario.getTenantId());
        despesa.setUsuarioId(usuario.getId());
        despesa.setCategoriaSaidaId(categoriaSaida.getId());
        despesa.setCategoriaDespesaId(categoriaEnergia != null ? categoriaEnergia.getId() : null);
        despesa.setValor(Dinheiro.emReais(request.valor()));
        despesa.setData(request.data());
        despesa.setDiaSemana(request.data().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
        despesa.setTipoGasto("DIARIO_SEMANAL");
        despesa.setDescricao("Recarga em " + (request.localRecarga() != null ? request.localRecarga() : "local nao informado"));
        despesa.setObservacao(request.observacao());

        if (request.notaFiscalBase64() != null && !request.notaFiscalBase64().isBlank()) {
            String conteudo = request.notaFiscalBase64().contains(",")
                    ? request.notaFiscalBase64().substring(request.notaFiscalBase64().indexOf(',') + 1)
                    : request.notaFiscalBase64();
            despesa.setNotaFiscal(java.util.Base64.getDecoder().decode(conteudo));
            despesa.setNotaFiscalNome(request.notaFiscalNome());
            despesa.setNotaFiscalTipo(request.notaFiscalTipo());
        }

        return despesa;
    }

    private CategoriaSaida obterOuCriarCategoriaRecarga(Usuario usuario) {
        return categoriaSaidaRepositorio.findByTenantIdAndNomeIgnoreCaseAndAtivoTrue(usuario.getTenantId(), CATEGORIA_RECARGA)
                .orElseGet(() -> {
                    CategoriaSaida categoria = new CategoriaSaida();
                    categoria.setTenantId(usuario.getTenantId());
                    categoria.setNome(CATEGORIA_RECARGA);
                    categoria.setDescricao("Recargas de energia do veiculo");
                    categoria.setTipo(CategoriaSaida.TipoCategoriaSaida.CUSTO_VARIAVEL);
                    return categoriaSaidaRepositorio.save(categoria);
                });
    }

    private CategoriaDespesa obterCategoriaEnergia(java.util.UUID tenantId) {
        return categoriaDespesaRepositorio.findByTenantIdAndAtivoTrueOrderByNomeAsc(tenantId).stream()
                .filter(c -> "Energia eletrica".equalsIgnoreCase(c.getNome()))
                .findFirst()
                .orElse(null);
    }

    private void aplicarNotaFiscal(Recarga recarga, RecargaRequest request) {
        if (request.notaFiscalBase64() == null || request.notaFiscalBase64().isBlank()) {
            return;
        }
        if (request.notaFiscalTipo() == null || !java.util.Set.of("application/pdf", "image/jpeg", "image/png").contains(request.notaFiscalTipo())) {
            throw new IllegalArgumentException("Nota fiscal deve ser PDF, JPG ou PNG.");
        }
        String conteudo = request.notaFiscalBase64().contains(",")
                ? request.notaFiscalBase64().substring(request.notaFiscalBase64().indexOf(',') + 1)
                : request.notaFiscalBase64();
        recarga.setNotaFiscal(java.util.Base64.getDecoder().decode(conteudo));
        recarga.setNotaFiscalNome(request.notaFiscalNome());
        recarga.setNotaFiscalTipo(request.notaFiscalTipo());
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

    @SuppressWarnings("unused")
    private BigDecimal obterTarifaEnergiaTenant(java.util.UUID tenantId) {
        Tenant tenant = tenantRepositorio.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant nao encontrado."));
        return tenant.getTarifaEnergiaKwh();
    }
}
