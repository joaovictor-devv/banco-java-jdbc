package com.joaovictor.service;

import com.joaovictor.model.Meta;
import com.joaovictor.repository.MetaRepository;

import java.math.BigDecimal;
import java.util.List;

public class MetaService {

    private final MetaRepository repository;

    public MetaService() {
        this.repository = new MetaRepository();
    }

    public Meta cadastrarMeta(BigDecimal valorAlvo,
                              String nome,
                              Integer prazoMeses,
                              BigDecimal valorInicial,
                              String prioridade,
                              String descricao) {

        validarMeta(nome, valorAlvo, prazoMeses, valorInicial, prioridade);

        Meta meta = new Meta(
                nome.trim(),
                valorAlvo,
                prazoMeses,
                valorInicial,
                prioridade.trim(),
                normalizarDescricao(descricao)
        );

        long id = repository.salvar(meta);
        meta.setId(id);
        return meta;
    }

    public Meta atualizarMeta(long id,
                              BigDecimal valorAlvo,
                              String nome,
                              Integer prazoMeses,
                              BigDecimal valorInicial,
                              String prioridade,
                              String descricao) {
        buscarPorId(id);
        validarMeta(nome, valorAlvo, prazoMeses, valorInicial, prioridade);

        Meta meta = new Meta(
                id,
                nome.trim(),
                valorAlvo,
                prazoMeses,
                valorInicial,
                prioridade.trim(),
                normalizarDescricao(descricao)
        );

        if (!repository.atualizar(id, meta)) {
            throw new IllegalArgumentException("Meta não encontrada para atualização.");
        }

        return meta;
    }

    public Meta atualizarProgresso(long id, BigDecimal valorAtual) {
        Meta meta = buscarPorId(id);

        if (valorAtual == null || valorAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor atual da meta não pode ser negativo.");
        }

        if (valorAtual.compareTo(meta.getValorAlvo()) > 0) {
            throw new IllegalArgumentException("O valor atual não pode ser maior que o valor alvo da meta.");
        }

        if (!repository.atualizarValorInicial(id, valorAtual)) {
            throw new IllegalArgumentException("Meta não encontrada para atualização do progresso.");
        }

        meta.setValorInicial(valorAtual);
        return meta;
    }

    public void validarMeta(String nome,
                            BigDecimal valorAlvo,
                            Integer prazoMeses,
                            BigDecimal valorInicial,
                            String prioridade) {
        validarCampos(nome, valorAlvo, prazoMeses, valorInicial, prioridade);
    }

    public List<Meta> listarMetas() {
        return repository.listarTodas();
    }

    public Meta buscarPorId(long id) {
        Meta meta = repository.buscarPorId(id);

        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada para o id informado.");
        }

        return meta;
    }

    public void excluirMeta(long id) {
        boolean excluiu = repository.excluirPorId(id);

        if (!excluiu) {
            throw new IllegalArgumentException("Meta não encontrada para exclusão.");
        }
    }

    private void validarCampos(String nome,
                               BigDecimal valorAlvo,
                               Integer prazoMeses,
                               BigDecimal valorInicial,
                               String prioridade) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da meta é obrigatório.");
        }

        if (nome.length() > 100) {
            throw new IllegalArgumentException("O nome da meta deve ter no máximo 100 caracteres.");
        }

        if (valorAlvo == null || valorAlvo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor alvo deve ser maior que zero.");
        }

        if (prazoMeses == null || prazoMeses <= 0) {
            throw new IllegalArgumentException("O prazo em meses deve ser maior que zero.");
        }

        if (valorInicial == null || valorInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor inicial não pode ser negativo.");
        }

        if (valorInicial.compareTo(valorAlvo) > 0) {
            throw new IllegalArgumentException("O valor inicial não pode ser maior que o valor alvo.");
        }

        if (prioridade == null || prioridade.isBlank()) {
            throw new IllegalArgumentException("A prioridade é obrigatória.");
        }

        if (prioridade.length() > 30) {
            throw new IllegalArgumentException("A prioridade deve ter no máximo 30 caracteres.");
        }
    }

    private String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }

        if (descricao.length() > 255) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 255 caracteres.");
        }

        return descricao.trim();
    }
}
