package com.rideprofit.dominio.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categoria_saida")
@Getter
@Setter
public class CategoriaSaida extends EntidadeBase {
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    @Column(name = "descricao", length = 500)
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoCategoriaSaida tipo;
    public enum TipoCategoriaSaida { CUSTO_FIXO, CUSTO_VARIAVEL }
}
