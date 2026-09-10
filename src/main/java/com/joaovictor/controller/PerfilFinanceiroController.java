package com.joaovictor.controller;

import com.joaovictor.dto.PerfilFinanceiroRequest;
import com.joaovictor.dto.SaldoAtualRequest;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.service.PerfilFinanceiroService;
import com.joaovictor.service.SituacaoFinanceiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil-financeiro")
public class PerfilFinanceiroController {

    private final PerfilFinanceiroService service;
    private final SituacaoFinanceiraService situacaoService;

    public PerfilFinanceiroController(PerfilFinanceiroService service,
                                      SituacaoFinanceiraService situacaoService) {
        this.service = service;
        this.situacaoService = situacaoService;
    }

    @PostMapping
    public ResponseEntity<PerfilFinanceiro> cadastrar(@RequestBody PerfilFinanceiroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados do perfil são obrigatórios.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.cadastrarPerfil(request.getNome(), request.getSaldoAtual()));
    }

    @GetMapping
    public ResponseEntity<PerfilFinanceiro> buscar() {
        return ResponseEntity.ok(service.buscarUltimoPerfil());
    }

    @PutMapping
    public ResponseEntity<PerfilFinanceiro> atualizar(@RequestBody PerfilFinanceiroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados do perfil são obrigatórios.");
        }

        return ResponseEntity.ok(service.atualizarPerfil(request.getNome(), request.getSaldoAtual()));
    }

    @PutMapping("/saldo")
    public ResponseEntity<PerfilFinanceiro> atualizarSaldo(@RequestBody SaldoAtualRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("O saldo atual é obrigatório.");
        }
        return ResponseEntity.ok(service.atualizarSaldoAtual(request.getSaldoAtual()));
    }

    @GetMapping("/situacao")
    public ResponseEntity<SituacaoFinanceira> situacao() {
        return ResponseEntity.ok(situacaoService.analisar());
    }
}
