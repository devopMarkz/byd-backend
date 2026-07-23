package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.dto.AlterarSenhaRequest;
import com.rideprofit.aplicacao.dto.AtualizarPerfilRequest;
import com.rideprofit.aplicacao.dto.PerfilResponse;
import com.rideprofit.infraestrutura.persistencia.UsuarioRepositorio;
import com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfil")
public class PerfilController {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<PerfilResponse> obter() {
        return ResponseEntity.ok(resposta(UsuarioAutenticadoService.obterUsuarioAutenticado()));
    }

    @PutMapping
    public ResponseEntity<PerfilResponse> atualizar(@Valid @RequestBody AtualizarPerfilRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        usuario.setNome(request.nome().trim());
        return ResponseEntity.ok(resposta(usuarioRepositorio.save(usuario)));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequest request) {
        var usuario = UsuarioAutenticadoService.obterUsuarioAutenticado();
        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) throw new IllegalArgumentException("Senha atual invalida.");
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepositorio.save(usuario);
        return ResponseEntity.noContent().build();
    }

    private PerfilResponse resposta(com.rideprofit.dominio.entidade.Usuario usuario) {
        return new PerfilResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
