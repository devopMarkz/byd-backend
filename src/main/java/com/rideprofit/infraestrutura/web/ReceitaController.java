package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.AtualizarReceita;
import com.rideprofit.aplicacao.casodeuso.ExcluirReceita;
import com.rideprofit.aplicacao.casodeuso.ListarReceitas;
import com.rideprofit.aplicacao.casodeuso.RegistrarReceita;
import com.rideprofit.aplicacao.dto.ReceitaRequest;
import com.rideprofit.aplicacao.dto.ReceitaResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    private final RegistrarReceita registrarReceita;
    private final ListarReceitas listarReceitas;
    private final ExcluirReceita excluirReceita;
    private final AtualizarReceita atualizarReceita;
    private final com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio receitaRepositorio;

    public ReceitaController(RegistrarReceita registrarReceita, ListarReceitas listarReceitas, ExcluirReceita excluirReceita, AtualizarReceita atualizarReceita, com.rideprofit.infraestrutura.persistencia.ReceitaRepositorio receitaRepositorio) {
        this.registrarReceita = registrarReceita;
        this.listarReceitas = listarReceitas;
        this.excluirReceita = excluirReceita;
        this.atualizarReceita = atualizarReceita;
        this.receitaRepositorio = receitaRepositorio;
    }

    @PostMapping
    public ResponseEntity<ReceitaResponse> registrar(@Valid @RequestBody ReceitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrarReceita.executar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody ReceitaRequest request) {
        return ResponseEntity.ok(atualizarReceita.executar(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now().withDayOfMonth(1) : inicio;
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        return ResponseEntity.ok(listarReceitas.executar(dataInicio, dataFim));
    }

    @GetMapping("/{id}/nota-fiscal")
    public ResponseEntity<byte[]> baixarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var receita = receitaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId()).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Entrada nao encontrada."));
        if (receita.getNotaFiscal() == null) throw new jakarta.persistence.EntityNotFoundException("Comprovante nao encontrado.");
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.parseMediaType(receita.getNotaFiscalTipo())).header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + receita.getNotaFiscalNome().replace("\"", "") + "\"").body(receita.getNotaFiscal());
    }

    @DeleteMapping("/{id}/nota-fiscal")
    public ResponseEntity<Void> apagarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var receita = receitaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId()).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Entrada nao encontrada."));
        receita.setNotaFiscal(null); receita.setNotaFiscalNome(null); receita.setNotaFiscalTipo(null); receitaRepositorio.save(receita);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirReceita.executar(id);
        return ResponseEntity.noContent().build();
    }
}
