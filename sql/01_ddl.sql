-- ============================================================================
-- Script DDL - Sistema de Ponto Eletrônico (Simplificado)
-- Criado em: 09/06/2026
-- Descrição: Criação completa do banco de dados: tabelas, índices, função,
--            views e procedure.
-- Banco: PostgreSQL 14+
-- Executar com: psql -U paulo -d ponto_eletronico -f sql/01_ddl.sql
-- ============================================================================

-- ============================================================================
-- 1. DROPS (ordem inversa de dependência: procedures → views → functions → tables)
-- ============================================================================

-- Procedures
DROP PROCEDURE IF EXISTS registrar_saida(INT, TIME);

-- Views
DROP VIEW IF EXISTS vw_atrasos;
DROP VIEW IF EXISTS vw_registro_completo;

-- Functions
DROP FUNCTION IF EXISTS calcular_horas(INT, DATE, DATE, VARCHAR);

-- Tables (ordem inversa de criação: FK references)
DROP TABLE IF EXISTS justificativa_ausencia;
DROP TABLE IF EXISTS registro_ponto;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS cargo;
DROP TABLE IF EXISTS departamento;

-- ============================================================================
-- 2. TABLES
-- ============================================================================

-- 2.1. departamento
-- Agrupa funcionários por unidade organizacional.
CREATE TABLE departamento (
    id_departamento SERIAL PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    descricao       TEXT
);

-- 2.2. cargo
-- Define o cargo e o salário base associado.
CREATE TABLE cargo (
    id_cargo    SERIAL PRIMARY KEY,
    titulo      VARCHAR(100) NOT NULL,
    salario_base NUMERIC(10,2) NOT NULL CHECK (salario_base > 0)
);

-- 2.3. funcionario
-- Dados pessoais e vínculo com departamento e cargo.
CREATE TABLE funcionario (
    id_funcionario  SERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    cpf             VARCHAR(14) UNIQUE NOT NULL,
    id_departamento INT NOT NULL REFERENCES departamento(id_departamento),
    id_cargo        INT NOT NULL REFERENCES cargo(id_cargo),
    data_admissao   DATE NOT NULL DEFAULT CURRENT_DATE
);

-- 2.4. registro_ponto
-- Registro diário de entrada e saída do funcionário.
CREATE TABLE registro_ponto (
    id_registro   SERIAL PRIMARY KEY,
    id_funcionario INT NOT NULL REFERENCES funcionario(id_funcionario),
    data          DATE NOT NULL,
    hora_entrada  TIME,
    hora_saida    TIME,
    observacao    TEXT,
    CONSTRAINT chk_horario CHECK (
        hora_saida IS NULL OR hora_entrada IS NULL OR hora_saida > hora_entrada
    )
);

-- 2.5. justificativa_ausencia
-- Justificativas para ausências (atestados, férias, etc.).
CREATE TABLE justificativa_ausencia (
    id_justificativa SERIAL PRIMARY KEY,
    id_funcionario   INT NOT NULL REFERENCES funcionario(id_funcionario),
    data             DATE NOT NULL,
    tipo             VARCHAR(30) NOT NULL CHECK (tipo IN ('atestado', 'ferias', 'pessoal', 'outro')),
    descricao        TEXT,
    aprovada         BOOLEAN DEFAULT FALSE
);

-- ============================================================================
-- 3. INDEXES
-- ============================================================================

-- Acelera buscas por funcionário nos registros de ponto.
CREATE INDEX idx_registro_ponto_funcionario ON registro_ponto(id_funcionario);

-- Acelera buscas por data nos registros de ponto.
CREATE INDEX idx_registro_ponto_data ON registro_ponto(data);

-- Acelera buscas por funcionário nas justificativas.
CREATE INDEX idx_justificativa_funcionario ON justificativa_ausencia(id_funcionario);

-- Acelera buscas por departamento nos funcionários.
CREATE INDEX idx_funcionario_departamento ON funcionario(id_departamento);

-- Acelera buscas por cargo nos funcionários.
CREATE INDEX idx_funcionario_cargo ON funcionario(id_cargo);

-- ============================================================================
-- 4. FUNCTION (PL/pgSQL)
-- ============================================================================

-- 4.1. calcular_horas
-- Unifica cálculo de horas trabalhadas e horas extras em uma única função.
-- Modo 'normal': soma todas as horas trabalhadas (entrada → saída).
-- Modo 'extras': soma apenas o que exceder 8 horas por dia.
CREATE OR REPLACE FUNCTION calcular_horas(
    p_id_funcionario INT,
    p_data_inicio    DATE,
    p_data_fim       DATE,
    p_modo           VARCHAR DEFAULT 'normal'
) RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    total_hours  NUMERIC := 0;
    record_hours NUMERIC;
    rec          RECORD;
BEGIN
    FOR rec IN
        SELECT hora_entrada, hora_saida
        FROM registro_ponto
        WHERE id_funcionario = p_id_funcionario
          AND data BETWEEN p_data_inicio AND p_data_fim
          AND hora_entrada IS NOT NULL
          AND hora_saida IS NOT NULL
    LOOP
        record_hours := EXTRACT(EPOCH FROM (rec.hora_saida - rec.hora_entrada)) / 3600.0;

        IF p_modo = 'extras' THEN
            -- Apenas o que exceder 8 horas diárias
            IF record_hours > 8 THEN
                total_hours := total_hours + (record_hours - 8);
            END IF;
        ELSE
            -- Todas as horas trabalhadas (modo 'normal')
            total_hours := total_hours + record_hours;
        END IF;
    END LOOP;

    RETURN total_hours;
END;
$$;

-- ============================================================================
-- 5. VIEWS
-- ============================================================================

-- 5.1. vw_registro_completo
-- Visão completa dos registros de ponto com dados do funcionário, departamento e cargo.
CREATE OR REPLACE VIEW vw_registro_completo AS
SELECT
    rp.id_registro,
    f.nome        AS funcionario,
    d.nome        AS departamento,
    c.titulo      AS cargo,
    rp.data,
    rp.hora_entrada,
    rp.hora_saida,
    rp.observacao
FROM registro_ponto rp
JOIN funcionario f   ON rp.id_funcionario = f.id_funcionario
JOIN departamento d  ON f.id_departamento = d.id_departamento
JOIN cargo c         ON f.id_cargo = c.id_cargo;

-- 5.2. vw_atrasos
-- Lista os registros onde o funcionário entrou após as 08:00, com os minutos de atraso.
CREATE OR REPLACE VIEW vw_atrasos AS
SELECT
    rp.id_registro,
    f.nome AS funcionario,
    rp.data,
    rp.hora_entrada,
    EXTRACT(EPOCH FROM (rp.hora_entrada - '08:00'::time)) / 60 AS minutos_atraso
FROM registro_ponto rp
JOIN funcionario f ON rp.id_funcionario = f.id_funcionario
WHERE rp.hora_entrada > '08:00'::time;

-- ============================================================================
-- 6. PROCEDURE (PL/pgSQL)
-- ============================================================================

-- 6.1. registrar_saida
-- Registra a hora de saída em um registro de ponto existente.
-- Demonstra validação (existe? hora_saida > hora_entrada?),
-- UPDATE condicional, RAISE EXCEPTION e RAISE NOTICE.
CREATE OR REPLACE PROCEDURE registrar_saida(
    p_id_registro INT,
    p_hora_saida  TIME
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_hora_entrada TIME;
BEGIN
    -- Obtém a hora de entrada atual do registro.
    SELECT hora_entrada INTO v_hora_entrada
    FROM registro_ponto
    WHERE id_registro = p_id_registro;

    -- Verifica se o registro existe.
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Registro de ponto não encontrado.';
    END IF;

    -- Valida que a hora de saída é posterior à hora de entrada.
    IF p_hora_saida <= v_hora_entrada THEN
        RAISE EXCEPTION 'Hora de saída deve ser posterior à hora de entrada.';
    END IF;

    -- Atualiza o registro com a hora de saída.
    UPDATE registro_ponto
    SET hora_saida = p_hora_saida
    WHERE id_registro = p_id_registro;

    RAISE NOTICE 'Saída registrada com sucesso para o registro %.', p_id_registro;
END;
$$;
