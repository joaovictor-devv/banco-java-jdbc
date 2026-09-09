package com.joaovictor.controller;

import com.joaovictor.dto.SimulacaoGastoRequest;
import com.joaovictor.dto.SimulacaoMetaRequest;
import com.joaovictor.model.AnaliseGasto;
import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.Meta;
import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.service.AnaliseFinanceiraService;
import com.joaovictor.service.AnaliseGastoService;
import com.joaovictor.service.AnaliseMetaService;
import com.joaovictor.service.MotorFinanceiroService;
import com.joaovictor.service.SituacaoFinanceiraService;
import com.joaovictor.service.SugestaoFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/analise")
public class AnaliseController {

    private final AnaliseFinanceiraService analiseService = new AnaliseFinanceiraService();
    private final SugestaoFinanceiraService sugestaoService = new SugestaoFinanceiraService();
    private final SituacaoFinanceiraService situacaoService = new SituacaoFinanceiraService();
    private final MotorFinanceiroService motorFinanceiroService = new MotorFinanceiroService();
    private final AnaliseGastoService analiseGastoService = new AnaliseGastoService();
    private final AnaliseMetaService analiseMetaService = new AnaliseMetaService();

    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiro> resumo() {
        return ResponseEntity.ok(analiseService.gerarResumoDoMesAtual());
    }

    @GetMapping("/situacao")
    public ResponseEntity<SituacaoFinanceira> situacao() {
        return ResponseEntity.ok(situacaoService.analisar());
    }

    @GetMapping("/sugestoes")
    public ResponseEntity<List<String>> sugestoes() {
        return ResponseEntity.ok(sugestaoService.gerarSugestoesDoMesAtual());
    }

    @GetMapping("/capacidade-gastos")
    public ResponseEntity<CapacidadeFinanceira> capacidadeGastos() {
        return ResponseEntity.ok(motorFinanceiroService.calcularCapacidade());
    }

    @PostMapping("/simular-gasto")
    public ResponseEntity<AnaliseGasto> simularGasto(@RequestBody SimulacaoGastoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da simulação são obrigatórios.");
        }

        return ResponseEntity.ok(analiseGastoService.analisar(request.getValor()));
    }

    @PostMapping("/simular-meta")
    public ResponseEntity<AnaliseMeta> simularMeta(@RequestBody SimulacaoMetaRequest request) {
        validarSimulacaoMeta(request);

        BigDecimal valorInicial = request.getValorInicial() != null
                ? request.getValorInicial()
                : BigDecimal.ZERO;

        String nome = request.getNome() == null || request.getNome().isBlank()
                ? "Meta simulada"
                : request.getNome().trim();

        String prioridade = request.getPrioridade() == null || request.getPrioridade().isBlank()
                ? "media"
                : request.getPrioridade().trim();

        Meta meta = new Meta(
                nome,
                request.getValorAlvo(),
                request.getPrazoMeses(),
                valorInicial,
                prioridade,
                request.getDescricao()
        );

        return ResponseEntity.ok(analiseMetaService.analisar(meta));
    }

    private void validarSimulacaoMeta(SimulacaoMetaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da simulação da meta são obrigatórios.");
        }

        if (request.getValorAlvo() == null || request.getValorAlvo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor alvo da meta deve ser maior que zero.");
        }

        if (request.getPrazoMeses() == null || request.getPrazoMeses() <= 0) {
            throw new IllegalArgumentException("O prazo da meta deve ser maior que zero.");
        }

        if (request.getValorInicial() != null && request.getValorInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor inicial da meta não pode ser negativo.");
        }

        if (request.getValorInicial() != null
                && request.getValorInicial().compareTo(request.getValorAlvo()) > 0) {
            throw new IllegalArgumentException("O valor inicial não pode ser maior que o valor alvo.");
        }
    }
}
