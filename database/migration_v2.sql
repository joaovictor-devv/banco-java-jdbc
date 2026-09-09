USE banco_app;

-- Adiciona saldo_atual apenas se a coluna ainda não existir.
SET @saldo_coluna_existe = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'banco_app'
      AND TABLE_NAME = 'perfil_financeiro'
      AND COLUMN_NAME = 'saldo_atual'
);

SET @sql_saldo = IF(
    @saldo_coluna_existe = 0,
    'ALTER TABLE perfil_financeiro ADD COLUMN saldo_atual DECIMAL(12,2) NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE stmt_saldo FROM @sql_saldo;
EXECUTE stmt_saldo;
DEALLOCATE PREPARE stmt_saldo;

CREATE TABLE IF NOT EXISTS revisoes_mensais (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mes_referencia VARCHAR(30) NOT NULL,
    gasto_inesperado BOOLEAN NOT NULL,
    valor_incorreto BOOLEAN NOT NULL,
    revisar_categorias BOOLEAN NOT NULL,
    observacoes VARCHAR(500)
);
