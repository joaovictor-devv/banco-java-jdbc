package com.joaovictor.service;

import com.joaovictor.exception.IAIndisponivelException;
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
    private final String apiKey;
    private final String model;
    private volatile OpenAIClient client;

    public OpenAIService(
            ContextoFinanceiroIAService contextoService,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-5-mini}") String model) {
        this.contextoService = contextoService;
        this.apiKey = apiKey;
        this.model = model;
    }

    public RespostaIA perguntar(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IAIndisponivelException(
                    "A IA está indisponível porque a variável OPENAI_API_KEY não foi configurada."
            );
        }

        String contexto = contextoService.montarTexto();
        String instrucoes = """
                Você é a IA financeira do sistema FinIA.

                REGRAS OBRIGATÓRIAS:
                1. Use exclusivamente os dados financeiros fornecidos pelo backend.
                2. Nunca invente renda, gastos, saldo, margem, metas ou valores.
                3. Os cálculos feitos pelo backend são a fonte de verdade.
                4. O saldo atual informado no perfil é a fonte para disponibilidade imediata de dinheiro.
                5. O histórico de transações é opcional e não deve substituir o saldo informado.
                6. Ao analisar uma compra, considere saldo atual, margem mensal e compromissos das metas.
                7. Ao analisar uma meta, considere valor mensal necessário, prazo, margem disponível e outras metas.
                8. Se uma decisão for inviável ou arriscada, avise claramente e explique o motivo.
                9. Se os dados forem insuficientes, diga exatamente o que não pode ser concluído.
                10. Não substitua um profissional financeiro e não prometa resultados financeiros.
                11. Responda sempre em português do Brasil, de forma clara, prática e objetiva.
                12. Não refaça ou altere números calculados pelo backend sem explicar explicitamente que se trata apenas de uma simulação.
                """;

        String input = instrucoes
                + "\n\nDADOS FINANCEIROS DO FINIA:\n"
                + contexto
                + "\n\nPERGUNTA DO USUÁRIO:\n"
                + pergunta.trim();

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
                throw new IAIndisponivelException("A IA respondeu sem conteúdo de texto utilizável.");
            }

            return new RespostaIA(true, resposta);
        } catch (IAIndisponivelException e) {
            throw e;
        } catch (Exception e) {
            throw new IAIndisponivelException(
                    "Não foi possível consultar a IA da OpenAI no momento.",
                    e
            );
        }
    }

    private OpenAIClient obterClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = OpenAIOkHttpClient.builder()
                            .apiKey(apiKey)
                            .build();
                }
            }
        }

        return client;
    }
}
