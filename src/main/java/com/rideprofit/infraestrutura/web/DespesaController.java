package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.AtualizarDespesa;
import com.rideprofit.aplicacao.casodeuso.ExcluirDespesa;
import com.rideprofit.aplicacao.casodeuso.ListarDespesas;
import com.rideprofit.aplicacao.casodeuso.RegistrarDespesa;
import com.rideprofit.aplicacao.dto.DespesaRequest;
import com.rideprofit.aplicacao.dto.DespesaResponse;
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
@RequestMapping("/despesas")
public class DespesaController {

    private final RegistrarDespesa registrarDespesa;
    private final ListarDespesas listarDespesas;
    private final ExcluirDespesa excluirDespesa;
    private final AtualizarDespesa atualizarDespesa;
    private final com.rideprofit.infraestrutura.persistencia.DespesaRepositorio despesaRepositorio;

    public DespesaController(RegistrarDespesa registrarDespesa, ListarDespesas listarDespesas, ExcluirDespesa excluirDespesa, AtualizarDespesa atualizarDespesa, com.rideprofit.infraestrutura.persistencia.DespesaRepositorio despesaRepositorio) {
        this.registrarDespesa = registrarDespesa;
        this.listarDespesas = listarDespesas;
        this.excluirDespesa = excluirDespesa;
        this.atualizarDespesa = atualizarDespesa;
        this.despesaRepositorio = despesaRepositorio;
    }

    @PostMapping
    public ResponseEntity<DespesaResponse> registrar(@Valid @RequestBody DespesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrarDespesa.executar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody DespesaRequest request) {
        return ResponseEntity.ok(atualizarDespesa.executar(id, request));
    }

    @GetMapping("/{id}/nota-fiscal")
    public ResponseEntity<byte[]> baixarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var despesa = despesaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId()).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Saida nao encontrada."));
        if (despesa.getNotaFiscal() == null) throw new jakarta.persistence.EntityNotFoundException("Nota fiscal nao encontrada.");
        String nome = despesa.getNotaFiscalNome() == null ? "nota_fiscal" : despesa.getNotaFiscalNome().replace("\"", "");
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.parseMediaType(despesa.getNotaFiscalTipo())).header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nome + "\"").body(despesa.getNotaFiscal());
    }

    @DeleteMapping("/{id}/nota-fiscal")
    public ResponseEntity<Void> apagarNotaFiscal(@PathVariable UUID id) {
        var usuario = com.rideprofit.infraestrutura.seguranca.UsuarioAutenticadoService.obterUsuarioAutenticado();
        var despesa = despesaRepositorio.findByIdAndTenantIdAndAtivoTrue(id, usuario.getTenantId()).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Saida nao encontrada."));
        despesa.setNotaFiscal(null); despesa.setNotaFiscalNome(null); despesa.setNotaFiscalTipo(null); despesaRepositorio.save(despesa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DespesaResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now().withDayOfMonth(1) : inicio;
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        return ResponseEntity.ok(listarDespesas.executar(dataInicio, dataFim));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirDespesa.executar(id);
        return ResponseEntity.noContent().build();
    }
}
