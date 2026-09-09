package com.joaovictor.service;

import com.joaovictor.model.RevisaoMensal;
import com.joaovictor.repository.RevisaoMensalRepository;

import java.util.List;

public class RevisaoMensalService {

    private final RevisaoMensalRepository repository;

    public RevisaoMensalService() {
        this.repository = new RevisaoMensalRepository();
    }

    public void cadastrarRevisao(String mesReferencia,
                                 boolean gastoInesperado,
                                 boolean valorIncorreto,
                                 boolean revisarCategorias,
                                 String observacoes) {
        validarCampos(mesReferencia, observacoes);

        RevisaoMensal revisao = new RevisaoMensal(
                mesReferencia.trim(),
                gastoInesperado,
                valorIncorreto,
                revisarCategorias,
                normalizarObservacoes(observacoes)
        );

        repository.salvar(revisao);
    }

    public List<RevisaoMensal> listarRevisoes() {
        return repository.listarTodas();
    }

    public RevisaoMensal buscarPorId(long id) {
        RevisaoMensal revisao = repository.buscarPorId(id);

        if (revisao == null) {
            throw new IllegalArgumentException("Revisão mensal não encontrada para o id informado.");
        }

        return revisao;
    }

    public void excluirRevisao(long id) {
        if (!repository.excluirPorId(id)) {
            throw new IllegalArgumentException("Revisão mensal não encontrada para exclusão.");
        }
    }

    private void validarCampos(String mesReferencia, String observacoes) {
        if (mesReferencia == null || mesReferencia.isBlank()) {
            throw new IllegalArgumentException("O mês de referência é obrigatório.");
        }

        if (mesReferencia.length() > 30) {
            throw new IllegalArgumentException("O mês de referência deve ter no máximo 30 caracteres.");
        }

        if (observacoes != null && observacoes.length() > 500) {
            throw new IllegalArgumentException("As observações devem ter no máximo 500 caracteres.");
        }
    }

    private String normalizarObservacoes(String observacoes) {
        return observacoes == null || observacoes.isBlank() ? null : observacoes.trim();
    }
}
