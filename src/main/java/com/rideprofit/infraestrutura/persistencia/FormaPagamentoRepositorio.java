package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormaPagamentoRepositorio extends JpaRepository<FormaPagamento, UUID> {
    List<FormaPagamento> findByTenantIdAndAtivoTrueOrderByNomeAsc(UUID tenantId);
    Optional<FormaPagamento> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
