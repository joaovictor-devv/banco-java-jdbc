USE banco_app;

-- Migração da estrutura antiga para o modelo final simplificado do FinIA.
-- Pode ser executada novamente sem tentar recriar/remover colunas que já foram migradas.
-- Antes de executar em um banco com dados importantes, faça um backup.

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

-- 2) Adiciona o total simplificado de gastos caso ainda não exista.
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

-- 3) Se o banco ainda possuir todas as categorias antigas, soma os valores
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

-- 4) Procedure temporária usada apenas para tornar a remoção de colunas segura.
DROP PROCEDURE IF EXISTS finia_drop_coluna_antiga;
DELIMITER //
CREATE PROCEDURE finia_drop_coluna_antiga(IN coluna VARCHAR(64))
BEGIN
    SET @coluna_existe = (
        SELECT COUNT(*)
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'perfil_financeiro'
          AND COLUMN_NAME = coluna
    );

    IF @coluna_existe > 0 THEN
        SET @sql_drop = CONCAT('ALTER TABLE perfil_financeiro DROP COLUMN `', coluna, '`');
        PREPARE stmt_drop FROM @sql_drop;
        EXECUTE stmt_drop;
        DEALLOCATE PREPARE stmt_drop;
    END IF;
END //
DELIMITER ;

CALL finia_drop_coluna_antiga('renda_extra');
CALL finia_drop_coluna_antiga('gasto_moradia');
CALL finia_drop_coluna_antiga('gasto_agua');
CALL finia_drop_coluna_antiga('gasto_energia');
CALL finia_drop_coluna_antiga('gasto_internet');
CALL finia_drop_coluna_antiga('gasto_transporte');
CALL finia_drop_coluna_antiga('gasto_alimentacao');
CALL finia_drop_coluna_antiga('outras_despesas');
CALL finia_drop_coluna_antiga('objetivo_principal');

DROP PROCEDURE finia_drop_coluna_antiga;

-- 5) Funcionalidades removidas do escopo final.
DROP TABLE IF EXISTS transacoes;
DROP TABLE IF EXISTS revisoes_mensais;
