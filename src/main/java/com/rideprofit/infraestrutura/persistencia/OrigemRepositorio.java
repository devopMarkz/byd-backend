package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Origem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrigemRepositorio extends JpaRepository<Origem, UUID> {
    List<Origem> findByTenantIdAndAtivoTrueOrderByNomeAsc(UUID tenantId);
    Optional<Origem> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
