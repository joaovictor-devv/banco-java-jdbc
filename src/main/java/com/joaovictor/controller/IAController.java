package com.joaovictor.controller;

import com.joaovictor.dto.PerguntaIARequest;
import com.joaovictor.model.RespostaIA;
import com.joaovictor.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
public class IAController {

    private final OpenAIService service;

    public IAController() {
        this.service = new OpenAIService();
    }

    @PostMapping("/perguntar")
    public ResponseEntity<RespostaIA> perguntar(@RequestBody PerguntaIARequest request) {
        return ResponseEntity.ok(service.perguntar(request.getPergunta()));
    }
}
