package com.joaovictor.controller;

import com.joaovictor.dto.SimulacaoFinanceiraRequest;
import com.joaovictor.model.ResultadoSimulacaoFinanceira;
import com.joaovictor.service.SimulacaoFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {

    private final SimulacaoFinanceiraService service = new SimulacaoFinanceiraService();

    @PostMapping
    public ResponseEntity<ResultadoSimulacaoFinanceira> simular(
            @RequestBody SimulacaoFinanceiraRequest request) {
        return ResponseEntity.ok(service.simular(request));
    }
}
