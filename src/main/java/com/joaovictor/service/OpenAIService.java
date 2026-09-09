package com.joaovictor.service;

import com.joaovictor.model.RespostaIA;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    private final ContextoFinanceiroIAService contextoService;
    private final String model;
    private volatile OpenAIClient client;

    public OpenAIService(
            ContextoFinanceiroIAService contextoService,
            @Value("${openai.model:gpt-5-mini}") String model) {
        this.contextoService = contextoService;
        this.model = model;
    }

    public RespostaIA perguntar(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        if (System.getenv("OPENAI_API_KEY") == null || System.getenv("OPENAI_API_KEY").isBlank()) {
            throw new IllegalStateException("A variável de ambiente OPENAI_API_KEY não foi configurada.");
        }

        String contexto = contextoService.montarTexto();
        String instrucoes = """
                Você é a IA financeira do sistema FinIA.

                REGRAS OBRIGATÓRIAS:
                1. Use exclusivamente os dados financeiros fornecidos pelo backend.
                2. Nunca invente renda, gastos, saldo, margem, metas ou valores.
                3. Os cálculos feitos pelo backend são a fonte de verdade.
                4. Ao analisar uma compra, diga se ela é recomendável considerando saldo, margem livre e compromissos.
                5. Ao analisar uma meta, considere valor mensal necessário, prazo, margem livre e outras metas.
                6. Se uma decisão for inviável ou arriscada, avise claramente e explique o motivo.
                7. Se os dados forem insuficientes, diga exatamente o que não pode ser concluído.
                8. Não substitua um profissional financeiro.
                9. Responda sempre em português do Brasil, de forma clara, prática e objetiva.
                10. Não diga que realizou cálculos que não estejam presentes nos dados recebidos.
                """;

        String input = instrucoes
                + "\n\nDADOS FINANCEIROS DO FINIA:\n"
                + contexto
                + "\n\nPERGUNTA DO USUÁRIO:\n"
                + pergunta;

        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input(input)
                    .model(model)
                    .build();

            Response response = obterClient().responses().create(params);

            String resposta = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .map(outputText -> outputText.text())
                    .reduce("", String::concat)
                    .trim();

            if (resposta.isBlank()) {
                throw new IllegalStateException("A IA não retornou uma resposta de texto.");
            }

            return new RespostaIA(true, resposta);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível consultar a IA da OpenAI.", e);
        }
    }

    private OpenAIClient obterClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = OpenAIOkHttpClient.fromEnv();
                }
            }
        }
        return client;
    }
}
