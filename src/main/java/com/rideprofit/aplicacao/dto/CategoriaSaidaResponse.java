package com.rideprofit.aplicacao.dto;

import com.rideprofit.dominio.entidade.CategoriaSaida.TipoCategoriaSaida;
import java.util.UUID;

public record CategoriaSaidaResponse(UUID id, String nome, String descricao, TipoCategoriaSaida tipo) {}
