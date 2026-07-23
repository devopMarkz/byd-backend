package com.rideprofit.infraestrutura.web;

import com.rideprofit.aplicacao.casodeuso.CalcularDashboardFinanceiro;
import com.rideprofit.aplicacao.casodeuso.ObterDashboardMensal;
import com.rideprofit.aplicacao.casodeuso.ObterDashboardSemanal;
import com.rideprofit.aplicacao.casodeuso.ObterDespesasPorCategoria;
import com.rideprofit.aplicacao.casodeuso.ObterDashboardPorPeriodo;
import com.rideprofit.aplicacao.dto.DashboardPeriodoResponse;
import com.rideprofit.aplicacao.dto.DashboardMensalResponse;
import com.rideprofit.aplicacao.dto.DashboardResponse;
import com.rideprofit.aplicacao.dto.DashboardSemanalResponse;
import com.rideprofit.aplicacao.dto.DespesaPorCategoriaResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final CalcularDashboardFinanceiro calcularDashboardFinanceiro;
    private final ObterDashboardSemanal obterDashboardSemanal;
    private final ObterDashboardMensal obterDashboardMensal;
    private final ObterDespesasPorCategoria obterDespesasPorCategoria;
    private final ObterDashboardPorPeriodo obterDashboardPorPeriodo;

    public DashboardController(CalcularDashboardFinanceiro calcularDashboardFinanceiro,
                               ObterDashboardSemanal obterDashboardSemanal,
                               ObterDashboardMensal obterDashboardMensal,
                               ObterDespesasPorCategoria obterDespesasPorCategoria,
                               ObterDashboardPorPeriodo obterDashboardPorPeriodo) {
        this.calcularDashboardFinanceiro = calcularDashboardFinanceiro;
        this.obterDashboardSemanal = obterDashboardSemanal;
        this.obterDashboardMensal = obterDashboardMensal;
        this.obterDespesasPorCategoria = obterDespesasPorCategoria;
        this.obterDashboardPorPeriodo = obterDashboardPorPeriodo;
    }

    @GetMapping("/financeiro")
    public ResponseEntity<DashboardResponse> financeiro(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        LocalDate dataConsulta = data == null ? LocalDate.now() : data;
        return ResponseEntity.ok(calcularDashboardFinanceiro.executar(dataConsulta));
    }

    @GetMapping("/semanal")
    public ResponseEntity<DashboardSemanalResponse> semanal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        LocalDate dataConsulta = dataReferencia == null ? LocalDate.now() : dataReferencia;
        return ResponseEntity.ok(obterDashboardSemanal.executar(dataConsulta));
    }

    @GetMapping("/mensal")
    public ResponseEntity<DashboardMensalResponse> mensal(
            @RequestParam(required = false) YearMonth mesReferencia) {
        YearMonth mesConsulta = mesReferencia == null ? YearMonth.now() : mesReferencia;
        return ResponseEntity.ok(obterDashboardMensal.executar(mesConsulta));
    }

    @GetMapping("/despesas-por-categoria")
    public ResponseEntity<List<DespesaPorCategoriaResponse>> despesasPorCategoria(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate dataInicio = inicio == null ? LocalDate.now().withDayOfMonth(1) : inicio;
        LocalDate dataFim = fim == null ? LocalDate.now() : fim;
        return ResponseEntity.ok(obterDespesasPorCategoria.executar(dataInicio, dataFim));
    }

    @GetMapping
    public ResponseEntity<DashboardPeriodoResponse> porPeriodo(
            @RequestParam(defaultValue = "MENSAL") String periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referencia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        LocalDate data = referencia == null ? LocalDate.now() : referencia;
        LocalDate dataInicio;
        LocalDate dataFim;
        switch (periodo.toUpperCase()) {
            case "DIARIO" -> { dataInicio = data; dataFim = data; }
            case "SEMANAL" -> { dataInicio = data.with(java.time.DayOfWeek.MONDAY); dataFim = dataInicio.plusDays(6); }
            case "ANUAL" -> { dataInicio = data.withDayOfYear(1); dataFim = data.withMonth(12).withDayOfMonth(31); }
            case "PERSONALIZADO" -> { if (inicio == null || fim == null) throw new IllegalArgumentException("Inicio e fim sao obrigatorios no periodo personalizado."); dataInicio = inicio; dataFim = fim; }
            case "MENSAL" -> { dataInicio = data.withDayOfMonth(1); dataFim = data.withDayOfMonth(data.lengthOfMonth()); }
            default -> throw new IllegalArgumentException("Periodo invalido. Use DIARIO, SEMANAL, MENSAL, ANUAL ou PERSONALIZADO.");
        }
        return ResponseEntity.ok(obterDashboardPorPeriodo.executar(dataInicio, dataFim));
    }
}
