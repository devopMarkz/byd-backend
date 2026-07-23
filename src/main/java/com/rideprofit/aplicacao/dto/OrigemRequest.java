package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;

public record OrigemRequest(@NotBlank String nome, String descricao, String imagemBase64) {}
