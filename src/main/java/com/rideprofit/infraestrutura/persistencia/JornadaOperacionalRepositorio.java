package com.rideprofit.infraestrutura.persistencia;

import com.rideprofit.dominio.entidade.JornadaOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JornadaOperacionalRepositorio extends JpaRepository<JornadaOperacional, UUID> {

    List<JornadaOperacional> findByTenantIdAndDataBetweenAndAtivoTrueOrderByDataDescHorarioInicioDesc(UUID tenantId, LocalDate inicio, LocalDate fim);

    Optional<JornadaOperacional> findFirstByStatusAndUsuarioIdOrderByDataDescHorarioInicioDesc(JornadaOperacional.StatusJornada status, UUID usuarioId);

    Optional<JornadaOperacional> findByIdAndTenantIdAndAtivoTrue(UUID id, UUID tenantId);
}
