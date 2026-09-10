package com.joaovictor.controller;

import com.joaovictor.dto.SimulacaoFinanceiraRequest;
import com.joaovictor.dto.StatusIAResponse;
import com.joaovictor.model.AnaliseIARequest;
import com.joaovictor.service.FiniaIAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ia")
public class FiniaIAController {

    private final FiniaIAService service;

    public FiniaIAController(FiniaIAService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<StatusIAResponse> status() {
        return ResponseEntity.ok(service.status());
    }

    @PostMapping("/perguntar")
    public ResponseEntity<Map<String, Object>> perguntar(@RequestBody AnaliseIARequest request) {
        String resposta = service.analisar(request);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "resposta", resposta
        ));
    }

    @PostMapping("/analisar")
    public ResponseEntity<Map<String, Object>> analisar(@RequestBody AnaliseIARequest request) {
        String resposta = service.analisar(request);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "resposta", resposta
        ));
    }

    @GetMapping("/analisar")
    public ResponseEntity<Map<String, Object>> analisarAutomaticamente() {
        AnaliseIARequest request = new AnaliseIARequest();
        request.setPergunta("Faça uma análise geral da minha situação financeira atual. " +
                "Mostre os principais pontos positivos, riscos, metas que exigem atenção " +
                "e as ações mais importantes que eu deveria considerar agora.");

        String resposta = service.analisar(request);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "resposta", resposta
        ));
    }

    @PostMapping("/explicar-simulacao")
    public ResponseEntity<Map<String, Object>> explicarSimulacao(
            @RequestBody SimulacaoFinanceiraRequest request) {
        String resposta = service.explicarSimulacao(request);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "resposta", resposta
        ));
    }
}
