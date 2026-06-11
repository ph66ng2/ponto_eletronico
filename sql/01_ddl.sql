

-- 1. REMOVER ESTRUTURAS ANTIGAS
DROP PROCEDURE IF EXISTS registrar_saida(INT, TIME);
DROP VIEW IF EXISTS vw_atrasos;
DROP VIEW IF EXISTS vw_registro_completo;
DROP FUNCTION IF EXISTS calcular_horas(INT, DATE, DATE, VARCHAR);
DROP TABLE IF EXISTS justificativa_ausencia;
DROP TABLE IF EXISTS registro_ponto;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS cargo;
DROP TABLE IF EXISTS departamento;

-- 2.  DDL

CREATE TABLE departamento (
    id_departamento SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT
);

CREATE TABLE cargo (
    id_cargo SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    salario_base NUMERIC(10,2) NOT NULL CHECK (salario_base > 0)
);

CREATE TABLE funcionario (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    id_departamento INT NOT NULL,
    id_cargo INT NOT NULL,
    data_admissao DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_func_depto FOREIGN KEY (id_departamento)
        REFERENCES departamento(id_departamento),
    CONSTRAINT fk_func_cargo FOREIGN KEY (id_cargo)
        REFERENCES cargo(id_cargo)
);

CREATE TABLE registro_ponto (
    id_registro SERIAL PRIMARY KEY,
    id_funcionario INT NOT NULL,
    data DATE NOT NULL,
    hora_entrada TIME,
    hora_saida TIME,
    observacao TEXT,
    CONSTRAINT fk_ponto_func FOREIGN KEY (id_funcionario)
        REFERENCES funcionario(id_funcionario),
    CONSTRAINT chk_horario CHECK (
        hora_saida IS NULL OR hora_entrada IS NULL
        OR hora_saida > hora_entrada
    )
);

CREATE TABLE justificativa_ausencia (
    id_justificativa SERIAL PRIMARY KEY,
    id_funcionario INT NOT NULL,
    data DATE NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('atestado', 'ferias', 'pessoal', 'outro')),
    descricao TEXT,
    aprovada BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_just_func FOREIGN KEY (id_funcionario)
        REFERENCES funcionario(id_funcionario)
);

-- 3. CRIAÇÃO DAS VIEWS

CREATE VIEW vw_registro_completo AS
SELECT
    rp.id_registro,
    f.nome AS funcionario,
    d.nome AS departamento,
    c.titulo AS cargo,
    rp.data,
    rp.hora_entrada,
    rp.hora_saida,
    rp.observacao
FROM registro_ponto rp
JOIN funcionario f   ON rp.id_funcionario = f.id_funcionario
JOIN departamento d  ON f.id_departamento = d.id_departamento
JOIN cargo c         ON f.id_cargo = c.id_cargo;

CREATE VIEW vw_atrasos AS
SELECT
    rp.id_registro,
    f.nome AS funcionario,
    rp.data,
    rp.hora_entrada,
    EXTRACT(EPOCH FROM (rp.hora_entrada - '08:00'::time)) / 60 AS minutos_atraso
FROM registro_ponto rp
JOIN funcionario f ON rp.id_funcionario = f.id_funcionario
WHERE rp.hora_entrada > '08:00'::time;

-- 4.  FUNCTION 

CREATE OR REPLACE FUNCTION calcular_horas(
    p_id_funcionario INT,
    p_data_inicio DATE,
    p_data_fim DATE,
    p_modo VARCHAR DEFAULT 'normal'
) RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    total NUMERIC := 0;
    horas_dia NUMERIC;
    rec RECORD;
BEGIN
    FOR rec IN
        SELECT hora_entrada, hora_saida
        FROM registro_ponto
        WHERE id_funcionario = p_id_funcionario
          AND data BETWEEN p_data_inicio AND p_data_fim
          AND hora_entrada IS NOT NULL
          AND hora_saida IS NOT NULL
    LOOP
        horas_dia := EXTRACT(EPOCH FROM (rec.hora_saida - rec.hora_entrada)) / 3600.0;

        IF p_modo = 'extras' THEN
            IF horas_dia > 8 THEN
                total := total + (horas_dia - 8);
            END IF;
        ELSE
            total := total + horas_dia;
        END IF;
    END LOOP;
    RETURN total;
END;
$$;

-- 5.  PROCEDURE

CREATE OR REPLACE PROCEDURE registrar_saida(
    p_id_registro INT,
    p_hora_saida TIME
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_hora_entrada TIME;
BEGIN
    SELECT hora_entrada INTO v_hora_entrada
    FROM registro_ponto
    WHERE id_registro = p_id_registro;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Registro de ponto não encontrado.';
    END IF;

    IF p_hora_saida <= v_hora_entrada THEN
        RAISE EXCEPTION 'Hora de saida deve ser posterior a hora de entrada.';
    END IF;

    UPDATE registro_ponto SET hora_saida = p_hora_saida
    WHERE id_registro = p_id_registro;

    RAISE NOTICE 'Saida registrada com sucesso para o registro %.', p_id_registro;
END;
$$;
