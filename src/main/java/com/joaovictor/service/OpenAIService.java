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

    public boolean isConfigurada() {
        return apiKey != null && !apiKey.isBlank();
    }

    public RespostaIA perguntar(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        if (!isConfigurada()) {
            throw new IAIndisponivelException(
                    "A IA está indisponível porque a variável OPENAI_API_KEY não foi configurada."
            );
        }

        String contexto = contextoService.montarTexto();
        String instrucoes = """
                Você é a FinIA, assistente de organização financeira pessoal do sistema FinIA.

                REGRAS OBRIGATÓRIAS:
                1. Os números e classificações calculados pelo backend são a fonte de verdade.
                2. Nunca invente saldo, renda, gastos, reserva, margem, capacidade, metas, prazos ou projeções.
                3. Não altere, substitua ou refaça silenciosamente os cálculos recebidos do motor financeiro.
                4. O saldo atual informado pelo usuário é a referência para disponibilidade imediata de dinheiro.
                5. Ao analisar um gasto, considere saldo, capacidade mensal e compromissos com metas.
                6. Ao analisar uma meta, considere valor restante, valor necessário por mês, prazo e impacto das demais metas.
                7. Se uma decisão for inviável, diga isso claramente e explique qual regra ou valor tornou a decisão inviável.
                8. Se houver um prazo mínimo ou confortável calculado pelo backend, use esses valores em vez de criar um prazo próprio.
                9. Se os dados disponíveis não permitirem uma conclusão, explique exatamente o que falta.
                10. O FinIA não é banco, corretora ou plataforma de investimentos. Não faça recomendação específica de investimento, ativo, ação, fundo ou criptomoeda.
                11. Não prometa resultados futuros e deixe claro quando algo for apenas uma projeção.
                12. Responda sempre em português do Brasil, de forma clara, prática e objetiva.
                13. Diferencie explicitamente fato calculado pelo motor de sugestão textual da FinIA quando isso puder gerar dúvida.
                """;

        String input = "DADOS FINANCEIROS CALCULADOS PELO BACKEND:\n"
                + contexto
                + "\n\nPERGUNTA DO USUÁRIO:\n"
                + pergunta.trim();

        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .instructions(instrucoes)
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
