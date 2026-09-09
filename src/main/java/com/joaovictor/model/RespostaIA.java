package com.joaovictor.model;

public class RespostaIA {

    private final boolean sucesso;
    private final String resposta;

    public RespostaIA(boolean sucesso, String resposta) {
        this.sucesso = sucesso;
        this.resposta = resposta;
    }

    public boolean isSucesso() { return sucesso; }
    public String getResposta() { return resposta; }
}
