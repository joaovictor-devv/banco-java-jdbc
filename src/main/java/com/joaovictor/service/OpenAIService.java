package com.joaovictor.service;

import com.joaovictor.model.RespostaIA;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAIService {

    private final ContextoFinanceiroIAService contextoService;
    private final OpenAIClient client;

    public OpenAIService() {
        this.contextoService = new ContextoFinanceiroIAService();
        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("A variável de ambiente OPENAI_API_KEY não foi configurada.");
        }

        this.client = OpenAIOkHttpClient.fromEnv();
    }

    public RespostaIA perguntar(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta é obrigatória.");
        }

        String contexto = contextoService.montarTexto();
        String instrucoes = "Você é a IA financeira do sistema FinIA. " +
                "Analise exclusivamente os dados financeiros fornecidos pelo sistema. " +
                "Não invente renda, gastos, saldo ou metas. Os cálculos financeiros já foram feitos pelo backend. " +
                "Use esses cálculos como fonte de verdade. " +
                "Se os dados não forem suficientes para responder, diga isso claramente. " +
                "Ao avaliar uma compra ou meta, considere a margem livre, o saldo, as despesas e as metas existentes. " +
                "Explique de forma simples, direta e em português do Brasil.\n\n" +
                "DADOS DO FINIA:\n" + contexto +
                "\nPERGUNTA DO USUÁRIO:\n" + pergunta;

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(instrucoes)
                .model(ChatModel.GPT_5_2)
                .build();

        Response response = client.responses().create(params);

        String resposta = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .reduce("", String::concat);

        if (resposta.isBlank()) {
            throw new IllegalStateException("A IA não retornou uma resposta de texto.");
        }

        return new RespostaIA(true, resposta);
    }
}
