package com.joaovictor.dto;

import com.joaovictor.model.AnaliseMeta;
import com.joaovictor.model.Meta;

public class MetaAnaliseResponse {

    private final Meta meta;
    private final AnaliseMeta analise;
    private final String mensagem;

    public MetaAnaliseResponse(Meta meta, AnaliseMeta analise, String mensagem) {
        this.meta = meta;
        this.analise = analise;
        this.mensagem = mensagem;
    }

    public Meta getMeta() { return meta; }
    public AnaliseMeta getAnalise() { return analise; }
    public String getMensagem() { return mensagem; }
}
