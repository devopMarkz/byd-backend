package com.rideprofit.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;

public record FormaPagamentoRequest(@NotBlank String nome) {}
