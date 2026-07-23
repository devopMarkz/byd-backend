package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Recarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecargaRepositorio extends JpaRepository<Recarga, UUID> {

    List<Recarga> findByDataBetweenAndAtivoTrueOrderByDataDesc(LocalDate inicio, LocalDate fim);

    List<Recarga> findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(UUID tenantId, LocalDate inicio, LocalDate fim);

    java.util.Optional<Recarga> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
