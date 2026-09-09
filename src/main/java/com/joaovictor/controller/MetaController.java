package com.joaovictor.controller;

import com.joaovictor.dto.ApiResponse;
import com.joaovictor.dto.MetaAnaliseResponse;
import com.joaovictor.dto.MetaRequest;
import com.joaovictor.dto.ProgressoMetaRequest;
import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.Meta;
import com.joaovictor.service.AnaliseMetaService;
import com.joaovictor.service.MetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas")
public class MetaController {

    private final MetaService service = new MetaService();
    private final AnaliseMetaService analiseMetaService = new AnaliseMetaService();

    @PostMapping
    public ResponseEntity<ApiResponse> cadastrar(@RequestBody MetaRequest request) {
        service.validarMeta(
                request.getNome(),
                request.getValorAlvo(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade()
        );

        Meta candidata = new Meta(
                request.getNome(),
                request.getValorAlvo(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade(),
                request.getDescricao()
        );

        AnaliseMeta analise = analiseMetaService.analisar(candidata);

        service.cadastrarMeta(
                request.getValorAlvo(),
                request.getNome(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade(),
                request.getDescricao()
        );

        String mensagem = "Meta cadastrada com sucesso. " + analise.getMensagem();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, mensagem));
    }

    @GetMapping
    public ResponseEntity<List<Meta>> listar() {
        return ResponseEntity.ok(service.listarMetas());
    }

    @GetMapping("/resumo")
    public ResponseEntity<List<MetaAnaliseResponse>> listarComAnalise() {
        List<MetaAnaliseResponse> resultado = service.listarMetas()
                .stream()
                .map(meta -> new MetaAnaliseResponse(
                        meta,
                        analiseMetaService.analisar(meta),
                        "Análise calculada com a situação financeira atual."
                ))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}/viabilidade")
    public ResponseEntity<AnaliseMeta> analisarViabilidade(@PathVariable long id) {
        return ResponseEntity.ok(analiseMetaService.analisar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meta> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaAnaliseResponse> atualizar(@PathVariable long id,
                                                          @RequestBody MetaRequest request) {
        Meta candidata = new Meta(
                id,
                request.getNome(),
                request.getValorAlvo(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade(),
                request.getDescricao()
        );

        service.validarMeta(
                request.getNome(),
                request.getValorAlvo(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade()
        );

        AnaliseMeta analise = analiseMetaService.analisar(candidata);
        Meta atualizada = service.atualizarMeta(
                id,
                request.getValorAlvo(),
                request.getNome(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade(),
                request.getDescricao()
        );

        return ResponseEntity.ok(new MetaAnaliseResponse(
                atualizada,
                analise,
                "Meta atualizada e reanalisada com sucesso."
        ));
    }

    @PatchMapping("/{id}/progresso")
    public ResponseEntity<MetaAnaliseResponse> atualizarProgresso(@PathVariable long id,
                                                                   @RequestBody ProgressoMetaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("O valor atual da meta é obrigatório.");
        }

        Meta atualizada = service.atualizarProgresso(id, request.getValorAtual());
        AnaliseMeta analise = analiseMetaService.analisar(atualizada);

        return ResponseEntity.ok(new MetaAnaliseResponse(
                atualizada,
                analise,
                "Progresso da meta atualizado com sucesso."
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> excluir(@PathVariable long id) {
        service.excluirMeta(id);
        return ResponseEntity.ok(new ApiResponse(true, "Meta excluída com sucesso."));
    }
}
