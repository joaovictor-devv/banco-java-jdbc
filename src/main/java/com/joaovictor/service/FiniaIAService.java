package com.joaovictor.service;

import com.joaovictor.model.AnaliseIARequest;
import com.joaovictor.model.RespostaIA;
import org.springframework.stereotype.Service;

@Service
public class FiniaIAService {

    private final OpenAIService openAIService;

    public FiniaIAService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    public String analisar(AnaliseIARequest request) {
        if (request == null || request.getPergunta() == null || request.getPergunta().isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        RespostaIA resposta = openAIService.perguntar(request.getPergunta());
        return resposta.getResposta();
    }
}
