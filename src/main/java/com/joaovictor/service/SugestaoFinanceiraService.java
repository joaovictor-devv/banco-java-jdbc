package com.joaovictor.service;

import com.joaovictor.model.CapacidadeFinanceira;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SugestaoFinanceiraService {

    private final MotorFinanceiroService motorFinanceiroService;

    public SugestaoFinanceiraService() {
        this.motorFinanceiroService = new MotorFinanceiroService();
    }

    public List<String> gerarSugestoesDoMesAtual() {
        CapacidadeFinanceira capacidade = motorFinanceiroService.calcularCapacidade();
        List<String> sugestoes = new ArrayList<>();

        switch (capacidade.getClassificacao()) {
            case "SEM_RENDA" -> sugestoes.add("Cadastre uma renda mensal para que o FinIA consiga calcular sua capacidade financeira.");
            case "GASTOS_ACIMA_DA_RENDA" -> sugestoes.add("Seus gastos mensais estão acima da renda. Reduza gastos antes de assumir novas metas ou compras.");
            case "RESERVA_INVIAVEL" -> sugestoes.add("O valor que você deseja guardar, somado aos gastos, ultrapassa sua renda mensal.");
            case "METAS_ACIMA_DA_CAPACIDADE" -> sugestoes.add("As metas atuais exigem mais do que a margem disponível. Revise prazos ou prioridades.");
            case "EQUILIBRADA" -> sugestoes.add("Toda a sua renda está comprometida. Evite novos gastos até criar margem no orçamento.");
            case "APERTADA" -> sugestoes.add("Seu orçamento ainda é possível, mas mais de 80% da renda está comprometida.");
            case "DEFICIT" -> sugestoes.add("O saldo atual está negativo. Priorize recuperar o saldo antes de novos gastos.");
            default -> sugestoes.add("Sua situação está saudável considerando gastos, reserva e metas atuais.");
        }

        if (capacidade.getComprometimentoMensalMetas().compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Suas metas exigem R$ " + capacidade.getComprometimentoMensalMetas()
                    + " por mês e deixam R$ " + capacidade.getMargemAposMetas() + " de margem mensal.");
        }

        if (capacidade.getCapacidadeGastoImediato().compareTo(BigDecimal.ZERO) > 0) {
            sugestoes.add("Considerando também o saldo atual, o limite de gasto imediato recomendado é de R$ "
                    + capacidade.getCapacidadeGastoImediato() + ".");
        }

        return sugestoes;
    }
}
