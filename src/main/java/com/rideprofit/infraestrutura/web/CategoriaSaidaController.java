package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.GerenciarCategoriasSaida;
import com.rideprofit.aplicacao.dto.CategoriaSaidaRequest;
import com.rideprofit.aplicacao.dto.CategoriaSaidaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/categorias-saida")
public class CategoriaSaidaController {
    private final GerenciarCategoriasSaida gerenciar;
    public CategoriaSaidaController(GerenciarCategoriasSaida gerenciar){this.gerenciar=gerenciar;}
    @GetMapping public List<CategoriaSaidaResponse> listar(){return gerenciar.listar();}
    @PostMapping public ResponseEntity<CategoriaSaidaResponse> criar(@Valid @RequestBody CategoriaSaidaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(gerenciar.criar(r));}
    @PutMapping("/{id}") public CategoriaSaidaResponse editar(@PathVariable UUID id,@Valid @RequestBody CategoriaSaidaRequest r){return gerenciar.editar(id,r);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id){gerenciar.excluir(id);return ResponseEntity.noContent().build();}
}
