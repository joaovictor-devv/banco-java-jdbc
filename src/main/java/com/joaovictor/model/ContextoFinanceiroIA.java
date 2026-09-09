package com.joaovictor.model;

import java.util.List;

public class ContextoFinanceiroIA {

    private final SituacaoFinanceira situacao;
    private final List<String> sugestoes;
    private final List<Meta> metas;

    public ContextoFinanceiroIA(SituacaoFinanceira situacao,
                                List<String> sugestoes,
                                List<Meta> metas) {
        this.situacao = situacao;
        this.sugestoes = sugestoes;
        this.metas = metas;
    }

    public SituacaoFinanceira getSituacao() { return situacao; }
    public List<String> getSugestoes() { return sugestoes; }
    public List<Meta> getMetas() { return metas; }
}
