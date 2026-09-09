package com.joaovictor.model;

import java.util.List;

public class ContextoFinanceiroIA {

    private final SituacaoFinanceira situacao;
    private final ResumoFinanceiro resumo;
    private final List<String> sugestoes;
    private final List<Meta> metas;

    public ContextoFinanceiroIA(SituacaoFinanceira situacao,
                                ResumoFinanceiro resumo,
                                List<String> sugestoes,
                                List<Meta> metas) {
        this.situacao = situacao;
        this.resumo = resumo;
        this.sugestoes = sugestoes;
        this.metas = metas;
    }

    public SituacaoFinanceira getSituacao() { return situacao; }
    public ResumoFinanceiro getResumo() { return resumo; }
    public List<String> getSugestoes() { return sugestoes; }
    public List<Meta> getMetas() { return metas; }
}
