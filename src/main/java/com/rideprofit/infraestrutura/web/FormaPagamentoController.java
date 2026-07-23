package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.GerenciarFormasPagamento;
import com.rideprofit.aplicacao.dto.FormaPagamentoRequest;
import com.rideprofit.aplicacao.dto.FormaPagamentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/formas-pagamento")
public class FormaPagamentoController {
    private final GerenciarFormasPagamento gerenciar;
    public FormaPagamentoController(GerenciarFormasPagamento gerenciar){this.gerenciar=gerenciar;}
    @GetMapping public List<FormaPagamentoResponse> listar(){return gerenciar.listar();}
    @PostMapping public ResponseEntity<FormaPagamentoResponse> criar(@Valid @RequestBody FormaPagamentoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(gerenciar.criar(r));}
    @PutMapping("/{id}") public FormaPagamentoResponse editar(@PathVariable UUID id,@Valid @RequestBody FormaPagamentoRequest r){return gerenciar.editar(id,r);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id){gerenciar.excluir(id);return ResponseEntity.noContent().build();}
}
