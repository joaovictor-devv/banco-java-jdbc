package com.joaovictor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OpenAiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public OpenAiService(
            ObjectMapper objectMapper,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-5-mini}") String model,
            @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    public String perguntar(String contextoFinanceiro, String pergunta) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("A chave da OpenAI não foi configurada. Defina OPENAI_API_KEY.");
        }

        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        String instrucoes = """
                Você é a IA financeira do FinIA.
                Sua função é orientar o usuário com base nos dados financeiros fornecidos pelo sistema.
                Nunca invente valores que não estejam nos dados.
                Sempre considere renda, despesas, saldo, margem livre e metas antes de recomendar algo.
                Se uma decisão for financeiramente arriscada, deixe isso claro.
                Não substitua um profissional financeiro.
                Responda em português do Brasil, de forma clara, prática e objetiva.
                """;

        String input = instrucoes
                + "\n\nDADOS FINANCEIROS ATUAIS:\n"
                + contextoFinanceiro
                + "\n\nPERGUNTA DO USUÁRIO:\n"
                + pergunta;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("input", input);

        try {
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/responses"))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Erro ao consultar a IA. HTTP " + response.statusCode() + ": " + response.body()
                );
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode outputText = root.path("output_text");

            if (!outputText.isMissingNode() && !outputText.asText().isBlank()) {
                return outputText.asText();
            }

            JsonNode output = root.path("output");
            for (JsonNode item : output) {
                for (JsonNode content : item.path("content")) {
                    JsonNode text = content.path("text");
                    if (!text.isMissingNode() && !text.asText().isBlank()) {
                        return text.asText();
                    }
                }
            }

            throw new IllegalStateException("A IA respondeu, mas não foi possível encontrar o texto da resposta.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("A consulta à IA foi interrompida.", e);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IllegalStateException("Não foi possível consultar a IA.", e);
        }
    }
}