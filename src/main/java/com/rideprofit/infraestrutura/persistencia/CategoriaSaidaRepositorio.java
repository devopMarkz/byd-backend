package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.CategoriaSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaSaidaRepositorio extends JpaRepository<CategoriaSaida, UUID> {
    List<CategoriaSaida> findByTenantIdAndAtivoTrueOrderByNomeAsc(UUID tenantId);
    Optional<CategoriaSaida> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);

    Optional<CategoriaSaida> findByTenantIdAndNomeIgnoreCaseAndAtivoTrue(UUID tenantId, String nome);
}
