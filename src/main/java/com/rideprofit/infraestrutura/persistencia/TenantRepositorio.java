package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepositorio extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByNomeIgnoreCase(String nome);
}
