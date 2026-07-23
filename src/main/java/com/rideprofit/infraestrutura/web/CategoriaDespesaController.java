package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.CriarCategoriaDespesa;
import com.rideprofit.aplicacao.casodeuso.DesativarCategoriaDespesa;
import com.rideprofit.aplicacao.casodeuso.EditarCategoriaDespesa;
import com.rideprofit.aplicacao.casodeuso.ListarCategoriasDespesaAtivas;
import com.rideprofit.aplicacao.dto.CategoriaDespesaRequest;
import com.rideprofit.aplicacao.dto.CategoriaDespesaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categorias-despesa")
public class CategoriaDespesaController {

    private final ListarCategoriasDespesaAtivas listarCategoriasDespesaAtivas;
    private final CriarCategoriaDespesa criarCategoriaDespesa;
    private final EditarCategoriaDespesa editarCategoriaDespesa;
    private final DesativarCategoriaDespesa desativarCategoriaDespesa;

    public CategoriaDespesaController(ListarCategoriasDespesaAtivas listarCategoriasDespesaAtivas,
                                      CriarCategoriaDespesa criarCategoriaDespesa,
                                      EditarCategoriaDespesa editarCategoriaDespesa,
                                      DesativarCategoriaDespesa desativarCategoriaDespesa) {
        this.listarCategoriasDespesaAtivas = listarCategoriasDespesaAtivas;
        this.criarCategoriaDespesa = criarCategoriaDespesa;
        this.editarCategoriaDespesa = editarCategoriaDespesa;
        this.desativarCategoriaDespesa = desativarCategoriaDespesa;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDespesaResponse>> listar() {
        return ResponseEntity.ok(listarCategoriasDespesaAtivas.executar());
    }

    @PostMapping
    public ResponseEntity<CategoriaDespesaResponse> criar(@Valid @RequestBody CategoriaDespesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarCategoriaDespesa.executar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDespesaResponse> editar(@PathVariable UUID id,
                                                           @Valid @RequestBody CategoriaDespesaRequest request) {
        return ResponseEntity.ok(editarCategoriaDespesa.executar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        desativarCategoriaDespesa.executar(id);
        return ResponseEntity.noContent().build();
    }
}
