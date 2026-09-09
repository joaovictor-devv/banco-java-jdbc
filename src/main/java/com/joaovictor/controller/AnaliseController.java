package com.joaovictor.controller;

import com.joaovictor.model.ResumoFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.service.AnaliseFinanceiraService;
import com.joaovictor.service.SituacaoFinanceiraService;
import com.joaovictor.service.SugestaoFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analise")
public class AnaliseController {

    private final AnaliseFinanceiraService analiseService = new AnaliseFinanceiraService();
    private final SugestaoFinanceiraService sugestaoService = new SugestaoFinanceiraService();
    private final SituacaoFinanceiraService situacaoService = new SituacaoFinanceiraService();

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
}
