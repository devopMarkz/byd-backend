package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.TenantRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ObterTarifaEnergiaTenant {

    private final TenantRepositorio tenantRepositorio;

    public ObterTarifaEnergiaTenant(TenantRepositorio tenantRepositorio) {
        this.tenantRepositorio = tenantRepositorio;
    }

    public BigDecimal executar() {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        UUID tenantId = usuario.getTenantId();
        return tenantRepositorio.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant nao encontrado."))
                .getTarifaEnergiaKwh();
    }
}
