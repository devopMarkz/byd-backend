package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface VeiculoRepositorio extends JpaRepository<Veiculo, UUID> {

    Optional<Veiculo> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);

    List<Veiculo> findByAtivoTrue();

    List<Veiculo> findByTenantIdAndAtivoTrue(UUID tenantId);
}
