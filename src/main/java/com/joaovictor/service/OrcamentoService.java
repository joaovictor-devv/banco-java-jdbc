package com.joaovictor.service;

import com.joaovictor.dto.OrcamentoRequest;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.OrcamentoResumo;
import com.joaovictor.model.PerfilFinanceiro;
import com.joaovictor.repository.PerfilFinanceiroRepository;

import java.math.BigDecimal;

public class OrcamentoService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final PerfilFinanceiroRepository perfilRepository;
    private final MotorFinanceiroService motorFinanceiroService;

    public OrcamentoService() {
        this.perfilRepository = new PerfilFinanceiroRepository();
        this.motorFinanceiroService = new MotorFinanceiroService();
    }

    public OrcamentoResumo buscarResumo() {
        CapacidadeFinanceira capacidade = motorFinanceiroService.calcularCapacidade();
        return montarResumo(capacidade);
    }

    public OrcamentoResumo salvar(OrcamentoRequest request) {
        validar(request);

        BigDecimal rendaExtra = valor(request.getRendaExtra());
        BigDecimal gastosMensais = valor(request.getGastosMensais());
        BigDecimal reserva = valor(request.getValorPlanejadoGuardar());

        PerfilFinanceiro existente = perfilRepository.buscarUltimoPerfil();

        if (existente == null) {
            PerfilFinanceiro novo = new PerfilFinanceiro(
                    request.getRendaMensal(),
                    rendaExtra,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    gastosMensais,
                    reserva,
                    "Organizar finanças",
                    ZERO
            );
            perfilRepository.salvar(novo);
        } else {
            String objetivo = existente.getObjetivoPrincipal();
            if (objetivo == null || objetivo.isBlank()) {
                objetivo = "Organizar finanças";
            }

            PerfilFinanceiro atualizado = new PerfilFinanceiro(
                    existente.getId(),
                    request.getRendaMensal(),
                    rendaExtra,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO,
                    gastosMensais,
                    reserva,
                    objetivo,
                    valor(existente.getSaldoAtual())
            );
            perfilRepository.atualizar(existente.getId(), atualizado);
        }

        return buscarResumo();
    }

    private OrcamentoResumo montarResumo(CapacidadeFinanceira capacidade) {
        return new OrcamentoResumo(
                capacidade.getRendaMensal(),
                capacidade.getRendaExtra(),
                capacidade.getRendaTotal(),
                capacidade.getDespesasPlanejadas(),
                capacidade.getReservaPlanejada(),
                capacidade.getMargemAntesMetas(),
                capacidade.getComprometimentoMensalMetas(),
                capacidade.getMargemAposMetas(),
                capacidade.getSaldoAtual(),
                capacidade.getCapacidadeGastoImediato(),
                capacidade.getPercentualRendaComprometida(),
                capacidade.getClassificacao()
        );
    }

    private void validar(OrcamentoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados do orçamento são obrigatórios.");
        }

        validarNaoNegativo(request.getRendaMensal(), "A renda mensal é obrigatória e não pode ser negativa.");
        validarNaoNegativo(valor(request.getRendaExtra()), "A renda extra não pode ser negativa.");
        validarNaoNegativo(valor(request.getGastosMensais()), "Os gastos mensais não podem ser negativos.");
        validarNaoNegativo(valor(request.getValorPlanejadoGuardar()), "O valor planejado para guardar não pode ser negativo.");
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
