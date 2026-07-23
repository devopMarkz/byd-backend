package com.rideprofit.dominio.entidade;

public enum Perfil {

    ROLE_MOTORISTA("Motorista"),
    ROLE_ADMINISTRADOR("Administrador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
