package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.CategoriaDespesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaDespesaRepositorio extends JpaRepository<CategoriaDespesa, UUID> {

    List<CategoriaDespesa> findByAtivoTrueOrderByNomeAsc();

    List<CategoriaDespesa> findByTenantIdAndAtivoTrueOrderByNomeAsc(UUID tenantId);
}
