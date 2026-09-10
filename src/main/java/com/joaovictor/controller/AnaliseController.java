package com.joaovictor.controller;

import com.joaovictor.dto.SimulacaoGastoRequest;
import com.joaovictor.dto.SimulacaoMetaRequest;
import com.joaovictor.model.AnaliseGasto;
import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.Meta;
import com.joaovictor.model.SituacaoFinanceira;
import com.joaovictor.service.AnaliseGastoService;
import com.joaovictor.service.AnaliseMetaService;
import com.joaovictor.service.MetaService;
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

    private final SugestaoFinanceiraService sugestaoService;
    private final SituacaoFinanceiraService situacaoService;
    private final MotorFinanceiroService motorFinanceiroService;
    private final AnaliseGastoService analiseGastoService;
    private final AnaliseMetaService analiseMetaService;
    private final MetaService metaService;

    public AnaliseController(SugestaoFinanceiraService sugestaoService,
                             SituacaoFinanceiraService situacaoService,
                             MotorFinanceiroService motorFinanceiroService,
                             AnaliseGastoService analiseGastoService,
                             AnaliseMetaService analiseMetaService,
                             MetaService metaService) {
        this.sugestaoService = sugestaoService;
        this.situacaoService = situacaoService;
        this.motorFinanceiroService = motorFinanceiroService;
        this.analiseGastoService = analiseGastoService;
        this.analiseMetaService = analiseMetaService;
        this.metaService = metaService;
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
        if (request == null) {
            throw new IllegalArgumentException("Os dados da simulação da meta são obrigatórios.");
        }

        BigDecimal valorInicial = request.getValorInicial() != null
                ? request.getValorInicial()
                : BigDecimal.ZERO;
        String nome = request.getNome() == null || request.getNome().isBlank()
                ? "Meta simulada"
                : request.getNome().trim();
        String prioridade = request.getPrioridade() == null || request.getPrioridade().isBlank()
                ? "media"
                : request.getPrioridade().trim();

        metaService.validarMeta(
                nome,
                request.getValorAlvo(),
                request.getPrazoMeses(),
                valorInicial,
                prioridade
        );

        if (request.getDescricao() != null && request.getDescricao().trim().length() > 255) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 255 caracteres.");
        }

        Meta meta = new Meta(
                nome,
                request.getValorAlvo(),
                request.getPrazoMeses(),
                valorInicial,
                prioridade,
                request.getDescricao() == null ? null : request.getDescricao().trim()
        );

        return ResponseEntity.ok(analiseMetaService.analisar(meta));
    }
}
