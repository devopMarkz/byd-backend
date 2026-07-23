package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DespesaRepositorio extends JpaRepository<Despesa, UUID> {

    List<Despesa> findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDesc(UUID tenantId, LocalDate inicio, LocalDate fim);

    boolean existsByTenantIdAndCategoriaSaidaIdAndAtivoTrue(UUID tenantId, UUID categoriaSaidaId);

    boolean existsByTenantIdAndFormaPagamentoIdAndAtivoTrue(UUID tenantId, UUID formaPagamentoId);

    java.util.Optional<Despesa> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
