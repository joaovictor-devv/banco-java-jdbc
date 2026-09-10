package com.joaovictor.dto;

public class StatusIAResponse {

    private final boolean configurada;
    private final String mensagem;

    public StatusIAResponse(boolean configurada, String mensagem) {
        this.configurada = configurada;
        this.mensagem = mensagem;
    }

    public boolean isConfigurada() {
        return configurada;
    }

    public String getMensagem() {
        return mensagem;
    }
}
