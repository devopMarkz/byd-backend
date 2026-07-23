package com.rideprofit.dominio.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario extends EntidadeBase implements UserDetails {

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "email", nullable = false, length = 200, unique = true)
    private String email;

    @Column(name = "senha", nullable = false, length = 500)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 50)
    private Perfil perfil;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(perfil.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAtivo();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAtivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isAtivo();
    }

    @Override
    public boolean isEnabled() {
        return isAtivo();
    }
}
