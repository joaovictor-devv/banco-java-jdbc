package com.joaovictor.service;

import com.joaovictor.dto.SimulacaoFinanceiraRequest;
import com.joaovictor.model.CapacidadeFinanceira;
import com.joaovictor.model.Meta;
import com.joaovictor.model.ProjecaoMensal;
import com.joaovictor.model.ProjecaoMeta;
import com.joaovictor.model.ResultadoSimulacaoFinanceira;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulacaoFinanceiraService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal LIMITE_ATENCAO_PERCENTUAL = new BigDecimal("80");

    private final MotorFinanceiroService motorFinanceiroService;
    private final MetaService metaService;
    private final CompromissoMetasService compromissoMetasService;

    public SimulacaoFinanceiraService() {
        this.motorFinanceiroService = new MotorFinanceiroService();
        this.metaService = new MetaService();
        this.compromissoMetasService = new CompromissoMetasService();
    }

    public ResultadoSimulacaoFinanceira simular(SimulacaoFinanceiraRequest request) {
        validar(request);

        CapacidadeFinanceira atual = motorFinanceiroService.calcularCapacidade();
        int meses = request.getMeses() != null ? request.getMeses() : 6;

        BigDecimal rendaMensal = escolher(request.getRendaMensal(), atual.getRendaMensal());
        BigDecimal rendaExtra = escolher(request.getRendaExtraMensal(), atual.getRendaExtra());
        BigDecimal gastosMensais = escolher(request.getGastosMensais(), atual.getDespesasPlanejadas());
        BigDecimal reservaMensal = valor(atual.getReservaPlanejada());
        BigDecimal gastoExtraordinario = valor(request.getGastoExtraordinario());
        BigDecimal aporteExtraMetasMensal = valor(request.getAporteExtraMetasMensal());
        int mesGastoExtraordinario = gastoExtraordinario.compareTo(BigDecimal.ZERO) > 0
                ? (request.getMesGastoExtraordinario() != null ? request.getMesGastoExtraordinario() : 1)
                : 0;

        List<Meta> metas = metaService.listarMetas();
        validarMetaPrioritaria(request.getMetaPrioritariaId(), metas);

        List<EstadoMeta> estadosMetas = prepararMetas(metas);
        List<ProjecaoMensal> evolucao = new ArrayList<>();

        BigDecimal saldo = valor(atual.getSaldoAtual());
        BigDecimal saldoInicial = saldo;
        BigDecimal totalAportadoMetas = BigDecimal.ZERO;
        BigDecimal totalReserva = BigDecimal.ZERO;
        BigDecimal totalGastosExtraordinarios = BigDecimal.ZERO;
        YearMonth mesBase = YearMonth.now();

        for (int indice = 1; indice <= meses; indice++) {
            YearMonth referencia = mesBase.plusMonths(indice);
            BigDecimal rendaTotal = rendaMensal.add(rendaExtra);

            BigDecimal aporteBaseMes = aplicarAportesBase(estadosMetas, indice, referencia);
            BigDecimal aporteExtraMes = aplicarAporteExtra(
                    aporteExtraMetasMensal,
                    estadosMetas,
                    request.getMetaPrioritariaId(),
                    indice,
                    referencia
            );

            BigDecimal gastoExtraMes = indice == mesGastoExtraordinario
                    ? gastoExtraordinario
                    : BigDecimal.ZERO;

            BigDecimal margemMensal = rendaTotal
                    .subtract(gastosMensais)
                    .subtract(reservaMensal)
                    .subtract(aporteBaseMes)
                    .subtract(aporteExtraMes)
                    .subtract(gastoExtraMes);

            saldo = saldo.add(margemMensal);

            String classificacao = classificarMes(
                    rendaTotal,
                    gastosMensais,
                    reservaMensal,
                    aporteBaseMes.add(aporteExtraMes),
                    gastoExtraMes,
                    margemMensal,
                    saldo
            );

            evolucao.add(new ProjecaoMensal(
                    indice,
                    referencia.toString(),
                    rendaTotal,
                    gastosMensais,
                    reservaMensal,
                    aporteBaseMes,
                    aporteExtraMes,
                    gastoExtraMes,
                    margemMensal,
                    saldo,
                    classificacao
            ));

            totalAportadoMetas = totalAportadoMetas.add(aporteBaseMes).add(aporteExtraMes);
            totalReserva = totalReserva.add(reservaMensal);
            totalGastosExtraordinarios = totalGastosExtraordinarios.add(gastoExtraMes);
        }

        List<ProjecaoMeta> projecoesMetas = montarProjecoesMetas(estadosMetas);
        String classificacaoFinal = evolucao.get(evolucao.size() - 1).getClassificacao();
        BigDecimal variacaoSaldo = saldo.subtract(saldoInicial);
        String mensagem = gerarMensagem(classificacaoFinal, saldo, variacaoSaldo, meses);
        String nomeCenario = request.getNomeCenario() == null || request.getNomeCenario().isBlank()
                ? "Simulação personalizada"
                : request.getNomeCenario().trim();

        return new ResultadoSimulacaoFinanceira(
                nomeCenario,
                meses,
                false,
                saldoInicial,
                saldo,
                variacaoSaldo,
                totalReserva,
                totalAportadoMetas,
                totalGastosExtraordinarios,
                classificacaoFinal,
                mensagem,
                atual,
                evolucao,
                projecoesMetas
        );
    }

    private List<EstadoMeta> prepararMetas(List<Meta> metas) {
        List<EstadoMeta> estados = new ArrayList<>();

        for (Meta meta : metas) {
            BigDecimal valorAtual = valor(meta.getValorInicial());
            BigDecimal aporteBase = compromissoMetasService.calcularValorMensal(meta);
            estados.add(new EstadoMeta(meta, valorAtual, aporteBase));
        }

        return estados;
    }

    private BigDecimal aplicarAportesBase(List<EstadoMeta> estados,
                                           int indiceMes,
                                           YearMonth referencia) {
        BigDecimal total = BigDecimal.ZERO;

        for (EstadoMeta estado : estados) {
            BigDecimal restante = restante(estado);
            if (restante.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal aporte = menor(estado.aporteBaseMensal, restante);
            estado.valorProjetado = estado.valorProjetado.add(aporte);
            total = total.add(aporte);
            registrarConclusao(estado, indiceMes, referencia);
        }

        return total;
    }

    private BigDecimal aplicarAporteExtra(BigDecimal aporteDisponivel,
                                           List<EstadoMeta> estados,
                                           Long metaPrioritariaId,
                                           int indiceMes,
                                           YearMonth referencia) {
        if (aporteDisponivel.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal restanteAporte = aporteDisponivel;
        BigDecimal totalAplicado = BigDecimal.ZERO;

        if (metaPrioritariaId != null) {
            EstadoMeta prioritaria = estados.stream()
                    .filter(estado -> estado.meta.getId() == metaPrioritariaId)
                    .findFirst()
                    .orElse(null);

            if (prioritaria != null) {
                BigDecimal aplicado = aplicarNaMeta(prioritaria, restanteAporte, indiceMes, referencia);
                totalAplicado = totalAplicado.add(aplicado);
                restanteAporte = restanteAporte.subtract(aplicado);
            }
        }

        if (restanteAporte.compareTo(BigDecimal.ZERO) <= 0) {
            return totalAplicado;
        }

        List<EstadoMeta> ordenadas = new ArrayList<>(estados);
        ordenadas.sort(Comparator
                .comparingInt((EstadoMeta estado) -> pesoPrioridade(estado.meta.getPrioridade()))
                .reversed()
                .thenComparingLong(estado -> estado.meta.getId()));

        for (EstadoMeta estado : ordenadas) {
            if (restanteAporte.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            if (metaPrioritariaId != null && estado.meta.getId() == metaPrioritariaId) {
                continue;
            }

            BigDecimal aplicado = aplicarNaMeta(estado, restanteAporte, indiceMes, referencia);
            totalAplicado = totalAplicado.add(aplicado);
            restanteAporte = restanteAporte.subtract(aplicado);
        }

        return totalAplicado;
    }

    private BigDecimal aplicarNaMeta(EstadoMeta estado,
                                     BigDecimal valorDisponivel,
                                     int indiceMes,
                                     YearMonth referencia) {
        BigDecimal restante = restante(estado);
        if (restante.compareTo(BigDecimal.ZERO) <= 0 || valorDisponivel.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal aplicado = menor(valorDisponivel, restante);
        estado.valorProjetado = estado.valorProjetado.add(aplicado);
        registrarConclusao(estado, indiceMes, referencia);
        return aplicado;
    }

    private void registrarConclusao(EstadoMeta estado, int indiceMes, YearMonth referencia) {
        if (estado.mesConclusao == null
                && estado.valorProjetado.compareTo(valor(estado.meta.getValorAlvo())) >= 0) {
            estado.mesConclusao = indiceMes;
            estado.mesConclusaoReferencia = referencia.toString();
        }
    }

    private List<ProjecaoMeta> montarProjecoesMetas(List<EstadoMeta> estados) {
        List<ProjecaoMeta> resultado = new ArrayList<>();

        for (EstadoMeta estado : estados) {
            BigDecimal valorAtual = valor(estado.meta.getValorInicial());
            BigDecimal valorAlvo = valor(estado.meta.getValorAlvo());
            BigDecimal valorProjetado = menor(estado.valorProjetado, valorAlvo);
            BigDecimal progressoAtual = percentual(valorAtual, valorAlvo);
            BigDecimal progressoProjetado = percentual(valorProjetado, valorAlvo);
            boolean concluida = estado.mesConclusao != null || valorProjetado.compareTo(valorAlvo) >= 0;

            String impacto;
            if (valorAtual.compareTo(valorAlvo) >= 0) {
                impacto = "A meta já estava concluída antes da simulação.";
            } else if (concluida) {
                impacto = "A meta seria concluída dentro do período simulado.";
            } else if (valorProjetado.compareTo(valorAtual) > 0) {
                impacto = "A meta avançaria R$ " + valorProjetado.subtract(valorAtual)
                        + " durante o período simulado.";
            } else {
                impacto = "Não houve avanço projetado para esta meta.";
            }

            resultado.add(new ProjecaoMeta(
                    estado.meta.getId(),
                    estado.meta.getNome(),
                    valorAtual,
                    valorAlvo,
                    valorProjetado,
                    progressoAtual,
                    progressoProjetado,
                    concluida,
                    estado.mesConclusao,
                    estado.mesConclusaoReferencia,
                    impacto
            ));
        }

        return resultado;
    }

    private String classificarMes(BigDecimal rendaTotal,
                                  BigDecimal gastosMensais,
                                  BigDecimal reserva,
                                  BigDecimal aporteMetas,
                                  BigDecimal gastoExtraordinario,
                                  BigDecimal margemMensal,
                                  BigDecimal saldoProjetado) {
        if (rendaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return "SEM_RENDA";
        }

        if (saldoProjetado.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT";
        }

        if (margemMensal.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFICIT_MENSAL";
        }

        if (margemMensal.compareTo(BigDecimal.ZERO) == 0) {
            return "EQUILIBRADA";
        }

        BigDecimal compromissos = gastosMensais
                .add(reserva)
                .add(aporteMetas)
                .add(gastoExtraordinario);

        BigDecimal percentualComprometido = compromissos.multiply(CEM)
                .divide(rendaTotal, 2, RoundingMode.HALF_UP);

        if (percentualComprometido.compareTo(LIMITE_ATENCAO_PERCENTUAL) >= 0) {
            return "APERTADA";
        }

        return "SAUDAVEL";
    }

    private String gerarMensagem(String classificacaoFinal,
                                 BigDecimal saldoFinal,
                                 BigDecimal variacaoSaldo,
                                 int meses) {
        return switch (classificacaoFinal) {
            case "DEFICIT", "DEFICIT_MENSAL" ->
                    "Neste cenário, a projeção termina em uma situação de déficit. Revise gastos, renda ou aportes antes de considerar este cenário seguro.";
            case "SEM_RENDA" ->
                    "A simulação não possui renda mensal suficiente para sustentar uma projeção financeira.";
            case "EQUILIBRADA" ->
                    "Ao final de " + meses + " meses, toda a renda projetada estaria comprometida, sem margem mensal para imprevistos.";
            case "APERTADA" ->
                    "O cenário permanece possível, mas termina com pouca margem financeira. O saldo projetado é de R$ " + saldoFinal + ".";
            default -> variacaoSaldo.compareTo(BigDecimal.ZERO) >= 0
                    ? "O cenário termina saudável após " + meses + " meses, com variação de saldo de R$ " + variacaoSaldo + "."
                    : "O cenário termina saudável, mas com redução de saldo de R$ " + variacaoSaldo.abs() + ".";
        };
    }

    private void validar(SimulacaoFinanceiraRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da simulação são obrigatórios.");
        }

        int meses = request.getMeses() != null ? request.getMeses() : 6;
        if (meses < 1 || meses > 60) {
            throw new IllegalArgumentException("O período da simulação deve estar entre 1 e 60 meses.");
        }

        validarNaoNegativoOpcional(request.getRendaMensal(), "A renda mensal simulada não pode ser negativa.");
        validarNaoNegativoOpcional(request.getRendaExtraMensal(), "A renda extra simulada não pode ser negativa.");
        validarNaoNegativoOpcional(request.getGastosMensais(), "Os gastos mensais simulados não podem ser negativos.");
        validarNaoNegativoOpcional(request.getGastoExtraordinario(), "O gasto extraordinário não pode ser negativo.");
        validarNaoNegativoOpcional(request.getAporteExtraMetasMensal(), "O aporte extra para metas não pode ser negativo.");

        if (valor(request.getGastoExtraordinario()).compareTo(BigDecimal.ZERO) > 0) {
            int mesExtra = request.getMesGastoExtraordinario() != null
                    ? request.getMesGastoExtraordinario()
                    : 1;
            if (mesExtra < 1 || mesExtra > meses) {
                throw new IllegalArgumentException("O mês do gasto extraordinário deve estar dentro do período simulado.");
            }
        }
    }

    private void validarMetaPrioritaria(Long metaId, List<Meta> metas) {
        if (metaId == null) {
            return;
        }

        boolean existe = metas.stream().anyMatch(meta -> meta.getId() == metaId);
        if (!existe) {
            throw new IllegalArgumentException("A meta prioritária informada não foi encontrada.");
        }
    }

    private void validarNaoNegativoOpcional(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private int pesoPrioridade(String prioridade) {
        if (prioridade == null) {
            return 1;
        }

        return switch (prioridade.trim().toLowerCase()) {
            case "alta" -> 3;
            case "media", "média" -> 2;
            default -> 1;
        };
    }

    private BigDecimal restante(EstadoMeta estado) {
        return valor(estado.meta.getValorAlvo()).subtract(estado.valorProjetado);
    }

    private BigDecimal percentual(BigDecimal parte, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal resultado = parte.multiply(CEM)
                .divide(total, 2, RoundingMode.HALF_UP);
        return resultado.compareTo(CEM) > 0 ? CEM : resultado;
    }

    private BigDecimal menor(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    private BigDecimal escolher(BigDecimal informado, BigDecimal padrao) {
        return informado != null ? informado : valor(padrao);
    }

    private BigDecimal valor(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private static class EstadoMeta {
        private final Meta meta;
        private BigDecimal valorProjetado;
        private final BigDecimal aporteBaseMensal;
        private Integer mesConclusao;
        private String mesConclusaoReferencia;

        private EstadoMeta(Meta meta, BigDecimal valorAtual, BigDecimal aporteBaseMensal) {
            this.meta = meta;
            this.valorProjetado = valorAtual;
            this.aporteBaseMensal = aporteBaseMensal;
        }
    }
}
