package com.rideprofit.aplicacao.dto;

import java.util.UUID;

public record OrigemResponse(UUID id, String nome, String descricao, String imagemBase64) {}
