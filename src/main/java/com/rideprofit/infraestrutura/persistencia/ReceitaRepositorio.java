package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReceitaRepositorio extends JpaRepository<Receita, UUID> {

    List<Receita> findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(UUID tenantId, LocalDate inicio, LocalDate fim);

    boolean existsByTenantIdAndOrigemIdAndAtivoTrue(UUID tenantId, UUID origemId);

    java.util.Optional<Receita> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
