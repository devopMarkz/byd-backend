package com.rideprofit.aplicacao.dto;

import com.rideprofit.dominio.entidade.CategoriaSaida.TipoCategoriaSaida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaSaidaRequest(@NotBlank String nome, String descricao, @NotNull TipoCategoriaSaida tipo) {}
