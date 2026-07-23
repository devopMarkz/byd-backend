package com.rideprofit.infraestrutura.seguranca;

import java.util.UUID;

public record UsuarioAutenticado(UUID id, UUID tenantId, String email) {
}
