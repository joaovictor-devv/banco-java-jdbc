package com.joaovictor.service;

import com.joaovictor.model.Meta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CompromissoMetasService {

    private final MetaService metaService;

    public CompromissoMetasService() {
        this.metaService = new MetaService();
    }

    public BigDecimal calcularValorMensal(Meta meta) {
        if (meta == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorAlvo = valor(meta.getValorAlvo());
        BigDecimal valorInicial = valor(meta.getValorInicial());
        BigDecimal restante = valorAlvo.subtract(valorInicial);

        if (restante.compareTo(BigDecimal.ZERO) <= 0 || meta.getPrazoMeses() <= 0) {
            return BigDecimal.ZERO;
        }

        return restante.divide(
                BigDecimal.valueOf(meta.getPrazoMeses()),
                2,
                RoundingMode.CEILING
        );
    }

    public BigDecimal calcularComprometimentoMensalTotal() {
        return calcularComprometimentoMensalExcluindo(null);
    }

    public BigDecimal calcularComprometimentoMensalExcluindo(Long metaId) {
        List<Meta> metas = metaService.listarMetas();
        BigDecimal total = BigDecimal.ZERO;

        for (Meta meta : metas) {
            if (metaId != null && meta.getId() == metaId) {
                continue;
            }

            total = total.add(calcularValorMensal(meta));
        }

        return total;
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
