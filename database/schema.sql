CREATE DATABASE IF NOT EXISTS banco_app;
USE banco_app;

CREATE TABLE IF NOT EXISTS perfil_financeiro (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL DEFAULT 'Usuário',
    saldo_atual DECIMAL(12,2) NOT NULL DEFAULT 0,
    renda_mensal DECIMAL(12,2) NOT NULL DEFAULT 0,
    gastos_mensais DECIMAL(12,2) NOT NULL DEFAULT 0,
    valor_planejado_guardar DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS metas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    valor_alvo DECIMAL(12,2) NOT NULL,
    prazo_meses INT NOT NULL,
    valor_inicial DECIMAL(12,2) NOT NULL DEFAULT 0,
    prioridade VARCHAR(30) NOT NULL,
    descricao VARCHAR(255)
);
