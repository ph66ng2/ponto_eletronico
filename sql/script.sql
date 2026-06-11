DROP PROCEDURE IF EXISTS registrar_saida(INT, TIME);
DROP VIEW IF EXISTS vw_registro_completo;
DROP VIEW IF EXISTS vw_atrasos;
DROP FUNCTION IF EXISTS calcular_horas(INT, DATE, DATE);
DROP FUNCTION IF EXISTS calcular_horas(INT, DATE, DATE, VARCHAR);
DROP TABLE IF EXISTS justificativa_ausencia;
DROP TABLE IF EXISTS registro_ponto;
DROP TABLE IF EXISTS funcionario;
DROP TABLE IF EXISTS cargo;
DROP TABLE IF EXISTS departamento;


--  CRIACAO DAS TABELAS


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


-- CRIACAO DA VIEW


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


-- CRIACAO DA FUNCTION


CREATE OR REPLACE FUNCTION calcular_horas(
    p_id_funcionario INT,
    p_data_inicio DATE,
    p_data_fim DATE
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
        total := total + horas_dia;
    END LOOP;
    RETURN total;
END;
$$;

-- CRIACAO DA PROCEDURE
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
        RAISE EXCEPTION 'Registro de ponto nao encontrado.';
    END IF;

    IF p_hora_saida <= v_hora_entrada THEN
        RAISE EXCEPTION 'Hora de saida deve ser posterior a hora de entrada.';
    END IF;

    UPDATE registro_ponto SET hora_saida = p_hora_saida
    WHERE id_registro = p_id_registro;

    RAISE NOTICE 'Saida registrada com sucesso para o registro %.', p_id_registro;
END;
$$;

-- POPULAR DADOS INICIAIS

TRUNCATE TABLE justificativa_ausencia, registro_ponto, funcionario, cargo, departamento
RESTART IDENTITY CASCADE;

INSERT INTO departamento (nome, descricao) VALUES
('Tecnologia da Informacao', 'Infraestrutura, sistemas e suporte tecnico'),
('Recursos Humanos', 'Gestao de pessoas, recrutamento e beneficios');

INSERT INTO cargo (titulo, salario_base) VALUES
('Analista de Sistemas', 7500.00),
('Suporte de TI', 3500.00);

INSERT INTO funcionario (nome, cpf, id_departamento, id_cargo, data_admissao) VALUES
('Joao Silva', '11122233344', 1, 1, '2023-03-15'),
('Maria Oliveira', '22233344455', 2, 2, '2024-01-10');

INSERT INTO registro_ponto (id_funcionario, data, hora_entrada, hora_saida, observacao) VALUES
(1, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(1, '2024-01-03', '08:15', '17:30', 'Chegou atrasado 15 min'),
(1, '2024-01-04', '08:00', '19:00', 'Hora extra'),
(2, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(2, '2024-01-03', '08:00', '17:00', 'Dia normal');

INSERT INTO justificativa_ausencia (id_funcionario, data, tipo, descricao, aprovada) VALUES
(1, '2024-01-15', 'atestado', 'Consulta medica de rotina', FALSE),
(2, '2024-01-20', 'ferias', 'Ferias programadas', TRUE),
(1, '2024-01-25', 'pessoal', 'Pendencias bancarias', FALSE);


--  OPERACOES CRUD


UPDATE justificativa_ausencia SET aprovada = TRUE WHERE id_justificativa = 1;
UPDATE cargo SET salario_base = 4000.00 WHERE id_cargo = 2;
DELETE FROM justificativa_ausencia WHERE aprovada = FALSE AND id_justificativa = 3;
DELETE FROM registro_ponto WHERE id_registro = 5;


--  CONSULTAS COM JOIN



SELECT f.nome AS funcionario, f.cpf,
       d.nome AS departamento,
       c.titulo AS cargo, c.salario_base,
       f.data_admissao
FROM funcionario f
JOIN departamento d ON f.id_departamento = d.id_departamento
JOIN cargo c        ON f.id_cargo = c.id_cargo
ORDER BY f.nome;


SELECT f.nome AS funcionario,
       d.nome AS departamento,
       c.titulo AS cargo,
       rp.data, rp.hora_entrada, rp.hora_saida, rp.observacao
FROM registro_ponto rp
JOIN funcionario f   ON rp.id_funcionario = f.id_funcionario
JOIN departamento d  ON f.id_departamento = d.id_departamento
JOIN cargo c         ON f.id_cargo = c.id_cargo
ORDER BY rp.data, f.nome;


SELECT f.nome AS funcionario, c.titulo AS cargo,
       ja.tipo, ja.descricao, ja.data, ja.aprovada
FROM justificativa_ausencia ja
JOIN funcionario f ON ja.id_funcionario = f.id_funcionario
JOIN cargo c       ON f.id_cargo = c.id_cargo
ORDER BY ja.data DESC;


--  USO DA FUNCTION


SELECT 'Joao Silva' AS funcionario,
       calcular_horas(1, '2024-01-01', '2024-01-31') AS total_horas;


--  USO DA PROCEDURE


CALL registrar_saida(1, '18:00');


--  CONSULTA A VIEW


SELECT * FROM vw_registro_completo ORDER BY data DESC;
