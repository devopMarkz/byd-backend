package com.rideprofit.aplicacao.casodeuso;

import com.rideprofit.aplicacao.dto.TarifaEnergiaRequest;
import com.rideprofit.dominio.entidade.Tenant;
import com.rideprofit.dominio.entidade.Usuario;
import com.rideprofit.infraestrutura.persistencia.TenantRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AtualizarTarifaEnergiaTenant {

    private final TenantRepositorio tenantRepositorio;

    public AtualizarTarifaEnergiaTenant(TenantRepositorio tenantRepositorio) {
        this.tenantRepositorio = tenantRepositorio;
    }

    public BigDecimal executar(TarifaEnergiaRequest request) {
        Usuario usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();

        Tenant tenant = tenantRepositorio.findById(usuario.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant nao encontrado."));

        tenant.setTarifaEnergiaKwh(request.tarifaEnergiaKwh());
        Tenant salvo = tenantRepositorio.save(tenant);
        return salvo.getTarifaEnergiaKwh();
    }
}
