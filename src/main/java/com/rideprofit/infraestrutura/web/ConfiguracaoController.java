package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.AtualizarTarifaEnergiaTenant;
import com.rideprofit.aplicacao.casodeuso.ObterTarifaEnergiaTenant;
import com.rideprofit.aplicacao.dto.TarifaEnergiaRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/configuracoes")
public class ConfiguracaoController {

    private final ObterTarifaEnergiaTenant obterTarifaEnergiaTenant;
    private final AtualizarTarifaEnergiaTenant atualizarTarifaEnergiaTenant;

    public ConfiguracaoController(ObterTarifaEnergiaTenant obterTarifaEnergiaTenant,
                                  AtualizarTarifaEnergiaTenant atualizarTarifaEnergiaTenant) {
        this.obterTarifaEnergiaTenant = obterTarifaEnergiaTenant;
        this.atualizarTarifaEnergiaTenant = atualizarTarifaEnergiaTenant;
    }

    @GetMapping("/tarifa-energia")
    public ResponseEntity<Map<String, BigDecimal>> obterTarifaEnergia() {
        return ResponseEntity.ok(Map.of("tarifaEnergiaKwh", obterTarifaEnergiaTenant.executar()));
    }

    @PutMapping("/tarifa-energia")
    public ResponseEntity<Map<String, BigDecimal>> atualizarTarifaEnergia(@Valid @RequestBody TarifaEnergiaRequest request) {
        return ResponseEntity.ok(Map.of("tarifaEnergiaKwh", atualizarTarifaEnergiaTenant.executar(request)));
    }
}
