package com.joaovictor.controller;

import com.joaovictor.dto.ApiResponse;
import com.joaovictor.dto.MetaRequest;
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
        Meta meta = service.cadastrarMeta(
                request.getValorAlvo(),
                request.getNome(),
                request.getPrazoMeses(),
                request.getValorInicial(),
                request.getPrioridade(),
                request.getDescricao()
        );

        AnaliseMeta analise = analiseMetaService.analisar(meta);
        String mensagem = "Meta cadastrada com sucesso. " + analise.getMensagem();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, mensagem));
    }

    @GetMapping
    public ResponseEntity<List<Meta>> listar() {
        return ResponseEntity.ok(service.listarMetas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meta> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/{id}/viabilidade")
    public ResponseEntity<AnaliseMeta> analisarViabilidade(@PathVariable long id) {
        return ResponseEntity.ok(analiseMetaService.analisar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> excluir(@PathVariable long id) {
        service.excluirMeta(id);
        return ResponseEntity.ok(new ApiResponse(true, "Meta excluída com sucesso."));
    }
}
