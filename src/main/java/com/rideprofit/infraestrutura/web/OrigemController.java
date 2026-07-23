package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.GerenciarOrigens;
import com.rideprofit.aplicacao.dto.OrigemRequest;
import com.rideprofit.aplicacao.dto.OrigemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/origens")
public class OrigemController {
    private final GerenciarOrigens gerenciar;
    public OrigemController(GerenciarOrigens gerenciar){this.gerenciar=gerenciar;}
    @GetMapping public List<OrigemResponse> listar(){return gerenciar.listar();}
    @PostMapping public ResponseEntity<OrigemResponse> criar(@Valid @RequestBody OrigemRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(gerenciar.criar(r));}
    @PutMapping("/{id}") public OrigemResponse editar(@PathVariable UUID id,@Valid @RequestBody OrigemRequest r){return gerenciar.editar(id,r);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id){gerenciar.excluir(id);return ResponseEntity.noContent().build();}
}
