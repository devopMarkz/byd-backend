package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.CadastrarVeiculo;
import com.rideprofit.aplicacao.casodeuso.ExcluirVeiculo;
import com.rideprofit.aplicacao.casodeuso.ListarVeiculosAtivos;
import com.rideprofit.aplicacao.dto.VeiculoRequest;
import com.rideprofit.aplicacao.dto.VeiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final CadastrarVeiculo cadastrarVeiculo;
    private final ListarVeiculosAtivos listarVeiculosAtivos;
    private final ExcluirVeiculo excluirVeiculo;

    public VeiculoController(CadastrarVeiculo cadastrarVeiculo, ListarVeiculosAtivos listarVeiculosAtivos, ExcluirVeiculo excluirVeiculo) {
        this.cadastrarVeiculo = cadastrarVeiculo;
        this.listarVeiculosAtivos = listarVeiculosAtivos;
        this.excluirVeiculo = excluirVeiculo;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarVeiculo.executar(request));
    }

    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> listar() {
        return ResponseEntity.ok(listarVeiculosAtivos.executar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirVeiculo.executar(id);
        return ResponseEntity.noContent().build();
    }
}
