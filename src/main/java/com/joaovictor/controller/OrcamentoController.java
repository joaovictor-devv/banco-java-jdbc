package com.joaovictor.controller;

import com.joaovictor.dto.OrcamentoRequest;
import com.joaovictor.model.OrcamentoResumo;
import com.joaovictor.service.OrcamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orcamento")
public class OrcamentoController {

    private final OrcamentoService service = new OrcamentoService();

    @GetMapping
    public ResponseEntity<OrcamentoResumo> buscar() {
        return ResponseEntity.ok(service.buscarResumo());
    }

    @PutMapping
    public ResponseEntity<OrcamentoResumo> salvar(@RequestBody OrcamentoRequest request) {
        return ResponseEntity.ok(service.salvar(request));
    }
}
