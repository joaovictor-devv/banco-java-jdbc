USE banco_app;

-- Migração única da estrutura antiga para o modelo final simplificado do FinIA.
-- Pode ser executada novamente sem tentar recriar/remover colunas já migradas.
-- Antes de executar em um banco com dados importantes, faça um backup.

-- Limpa uma procedure que possa ter ficado de uma execução antiga/interrompida.
DROP PROCEDURE IF EXISTS finia_drop_coluna_antiga;

-- Guarda a configuração atual do Safe Update Mode desta sessão.
SET @finia_safe_updates_original = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- 1) Adiciona nome caso ainda não exista.
SET @nome_existe = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'perfil_financeiro'
      AND COLUMN_NAME = 'nome'
);

SET @sql = IF(
    @nome_existe = 0,
    "ALTER TABLE perfil_financeiro ADD COLUMN nome VARCHAR(100) NOT NULL DEFAULT 'Usuário' AFTER id",
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Garante saldo_atual mesmo em bancos que não passaram pela migration_v2.
SET @saldo_atual_existe = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'perfil_financeiro'
      AND COLUMN_NAME = 'saldo_atual'
);

SET @sql = IF(
    @saldo_atual_existe = 0,
    'ALTER TABLE perfil_financeiro ADD COLUMN saldo_atual DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER nome',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) Adiciona o total simplificado de gastos caso ainda não exista.
SET @gastos_mensais_existe = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'perfil_financeiro'
      AND COLUMN_NAME = 'gastos_mensais'
);

SET @sql = IF(
    @gastos_mensais_existe = 0,
    'ALTER TABLE perfil_financeiro ADD COLUMN gastos_mensais DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER renda_mensal',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4) Se o banco ainda possuir todas as categorias antigas, soma os valores
-- para preservar o total mensal antes de remover as colunas antigas.
SET @categorias_antigas = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'perfil_financeiro'
      AND COLUMN_NAME IN (
          'gasto_moradia',
          'gasto_agua',
          'gasto_energia',
          'gasto_internet',
          'gasto_transporte',
          'gasto_alimentacao',
          'outras_despesas'
      )
);

SET @sql = IF(
    @categorias_antigas = 7,
    'UPDATE perfil_financeiro SET gastos_mensais = COALESCE(gasto_moradia, 0) + COALESCE(gasto_agua, 0) + COALESCE(gasto_energia, 0) + COALESCE(gasto_internet, 0) + COALESCE(gasto_transporte, 0) + COALESCE(gasto_alimentacao, 0) + COALESCE(outras_despesas, 0)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5) Remove com segurança cada coluna antiga, sem procedures nem DELIMITER.
SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'renda_extra');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `renda_extra`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_moradia');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_moradia`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_agua');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_agua`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_energia');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_energia`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_internet');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_internet`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_transporte');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_transporte`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'gasto_alimentacao');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `gasto_alimentacao`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'outras_despesas');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `outras_despesas`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @coluna_existe = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'perfil_financeiro' AND COLUMN_NAME = 'objetivo_principal');
SET @sql = IF(@coluna_existe > 0, 'ALTER TABLE perfil_financeiro DROP COLUMN `objetivo_principal`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 6) Funcionalidades removidas do escopo final.
DROP TABLE IF EXISTS transacoes;
DROP TABLE IF EXISTS revisoes_mensais;

-- Restaura o Safe Update Mode para o valor que estava antes da migração.
SET SQL_SAFE_UPDATES = @finia_safe_updates_original;
