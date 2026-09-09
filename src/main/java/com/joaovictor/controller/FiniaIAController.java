package com.joaovictor.controller;

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

    @PostMapping("/analisar")
    public ResponseEntity<Map<String, String>> analisar(@RequestBody AnaliseIARequest request) {
        String resposta = service.analisar(request);
        return ResponseEntity.ok(Map.of("resposta", resposta));
    }
}