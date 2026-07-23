package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.*;
import com.rideprofit.aplicacao.dto.AtualizarJornadaRequest;
import com.rideprofit.aplicacao.dto.EncerrarJornadaRequest;
import com.rideprofit.aplicacao.dto.IniciarJornadaRequest;
import com.rideprofit.aplicacao.dto.JornadaResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jornadas")
public class JornadaController {

    private final IniciarJornada iniciarJornada;
    private final EncerrarJornada encerrarJornada;
    private final ListarJornadas listarJornadas;
    private final AtualizarJornada atualizarJornada;
    private final ControlarJornadaRapida controlarJornadaRapida;
    private final ExcluirJornada excluirJornada;

    public JornadaController(IniciarJornada iniciarJornada,
                             EncerrarJornada encerrarJornada,
                             ListarJornadas listarJornadas,
                             AtualizarJornada atualizarJornada,
                             ExcluirJornada excluirJornada,
                             ControlarJornadaRapida controlarJornadaRapida) {
        this.iniciarJornada = iniciarJornada;
        this.encerrarJornada = encerrarJornada;
        this.listarJornadas = listarJornadas;
        this.atualizarJornada = atualizarJornada;
        this.excluirJornada = excluirJornada;
        this.controlarJornadaRapida = controlarJornadaRapida;
    }

    @PostMapping("/iniciar-agora")
    public ResponseEntity<JornadaResponse> iniciarAgora() { return ResponseEntity.status(HttpStatus.CREATED).body(controlarJornadaRapida.iniciar()); }

    @PostMapping("/encerrar-atual")
    public ResponseEntity<JornadaResponse> encerrarAtual() { return ResponseEntity.ok(controlarJornadaRapida.encerrar()); }

    @PostMapping("/inicio")
    public ResponseEntity<JornadaResponse> iniciar(@Valid @RequestBody IniciarJornadaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iniciarJornada.executar(request));
    }

    @PostMapping("/fim")
    public ResponseEntity<JornadaResponse> encerrar(@Valid @RequestBody EncerrarJornadaRequest request) {
        return ResponseEntity.ok(encerrarJornada.executar(request));
    }

    @GetMapping
    public ResponseEntity<List<JornadaResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now().withDayOfMonth(1) : inicio;
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        return ResponseEntity.ok(listarJornadas.executar(dataInicio, dataFim));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JornadaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarJornadaRequest request) {

        return ResponseEntity.ok(atualizarJornada.executar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirJornada.executar(id);
        return ResponseEntity.noContent().build();
    }
}
