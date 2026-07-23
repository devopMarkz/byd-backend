package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.AtualizarRecarga;
import com.rideprofit.aplicacao.casodeuso.ExcluirRecarga;
import com.rideprofit.aplicacao.casodeuso.ListarRecargas;
import com.rideprofit.aplicacao.casodeuso.RegistrarRecarga;
import com.rideprofit.aplicacao.dto.RecargaRequest;
import com.rideprofit.aplicacao.dto.RecargaResponse;
import com.rideprofit.infraestrutura.persistencia.RecargaRepositorio;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recargas")
public class RecargaController {

    private final RegistrarRecarga registrarRecarga;
    private final ListarRecargas listarRecargas;
    private final ExcluirRecarga excluirRecarga;
    private final RecargaRepositorio recargaRepositorio;
    private final AtualizarRecarga atualizarRecarga;

    public RecargaController(RegistrarRecarga registrarRecarga,
                             ListarRecargas listarRecargas,
                             ExcluirRecarga excluirRecarga,
                             RecargaRepositorio recargaRepositorio,
                             AtualizarRecarga atualizarRecarga) {
        this.registrarRecarga = registrarRecarga;
        this.listarRecargas = listarRecargas;
        this.excluirRecarga = excluirRecarga;
        this.recargaRepositorio = recargaRepositorio;
        this.atualizarRecarga = atualizarRecarga;
    }

    @PostMapping
    public ResponseEntity<RecargaResponse> registrar(@Valid @RequestBody RecargaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrarRecarga.executar(request));
    }

    @GetMapping
    public ResponseEntity<List<RecargaResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now().withDayOfMonth(1) : inicio;
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        return ResponseEntity.ok(listarRecargas.executar(dataInicio, dataFim));
    }

    @GetMapping("/{id}/nota-fiscal")
    public ResponseEntity<byte[]> baixarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var recarga = recargaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Recarga nao encontrada."));
        if (recarga.getNotaFiscal() == null) {
            throw new jakarta.persistence.EntityNotFoundException("Nota fiscal nao encontrada.");
        }
        String nome = recarga.getNotaFiscalNome() == null ? "nota_fiscal" : recarga.getNotaFiscalNome().replace("\"", "");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(recarga.getNotaFiscalTipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nome + "\"")
                .body(recarga.getNotaFiscal());
    }

    @DeleteMapping("/{id}/nota-fiscal")
    public ResponseEntity<Void> apagarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var recarga = recargaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Recarga nao encontrada."));
        recarga.setNotaFiscal(null);
        recarga.setNotaFiscalNome(null);
        recarga.setNotaFiscalTipo(null);
        recargaRepositorio.save(recarga);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirRecarga.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecargaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RecargaRequest request) {

        return ResponseEntity.ok(
                atualizarRecarga.executar(id, request)
        );
    }
}
