package com.rideprofit.dominio.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "forma_pagamento")
@Getter
@Setter
public class FormaPagamento extends EntidadeBase {
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
}
