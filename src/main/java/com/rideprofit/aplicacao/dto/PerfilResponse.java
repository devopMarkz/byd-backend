package com.rideprofit.aplicacao.dto;

import java.util.UUID;

public record PerfilResponse(UUID id, String nome, String email, String perfil) {
}
