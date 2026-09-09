USE banco_app;

-- Migração única da estrutura antiga para o modelo final do FinIA.
-- Execute este arquivo apenas uma vez em bancos que ainda possuem as colunas antigas.

ALTER TABLE perfil_financeiro
    ADD COLUMN nome VARCHAR(100) NOT NULL DEFAULT 'Usuário' AFTER id,
    ADD COLUMN gastos_mensais DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER renda_mensal;

UPDATE perfil_financeiro
SET gastos_mensais =
      COALESCE(gasto_moradia, 0)
    + COALESCE(gasto_agua, 0)
    + COALESCE(gasto_energia, 0)
    + COALESCE(gasto_internet, 0)
    + COALESCE(gasto_transporte, 0)
    + COALESCE(gasto_alimentacao, 0)
    + COALESCE(outras_despesas, 0);

ALTER TABLE perfil_financeiro
    DROP COLUMN renda_extra,
    DROP COLUMN gasto_moradia,
    DROP COLUMN gasto_agua,
    DROP COLUMN gasto_energia,
    DROP COLUMN gasto_internet,
    DROP COLUMN gasto_transporte,
    DROP COLUMN gasto_alimentacao,
    DROP COLUMN outras_despesas,
    DROP COLUMN objetivo_principal;

DROP TABLE IF EXISTS transacoes;
DROP TABLE IF EXISTS revisoes_mensais;
