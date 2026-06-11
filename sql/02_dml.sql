
TRUNCATE TABLE justificativa_ausencia, registro_ponto, funcionario, cargo, departamento
RESTART IDENTITY CASCADE;

-- 1 DADOS INICIAIS

INSERT INTO departamento (nome, descricao) VALUES
('Tecnologia da Informacao', 'Infraestrutura, sistemas e suporte tecnico'),
('Recursos Humanos', 'Gestao de pessoas, recrutamento e beneficios');

INSERT INTO cargo (titulo, salario_base) VALUES
('Analista de Sistemas', 7500.00),
('Suporte de TI', 3500.00);

INSERT INTO funcionario (nome, cpf, id_departamento, id_cargo, data_admissao) VALUES
('Joao Silva', '111.222.333-44', 1, 1, '2023-03-15'),
('Maria Oliveira', '222.333.444-55', 2, 2, '2024-01-10');

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

-- 2. UPDATE

UPDATE justificativa_ausencia SET aprovada = TRUE WHERE id_justificativa = 1;
UPDATE cargo SET salario_base = 4000.00 WHERE id_cargo = 2;

-- 3. DELETE

DELETE FROM justificativa_ausencia WHERE aprovada = FALSE AND id_justificativa = 3;
DELETE FROM registro_ponto WHERE id_registro = 5;

-- 4.JOIN

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

-- 5. FUNCTION 

SELECT 'Joao Silva' AS funcionario,
       calcular_horas(1, '2024-01-01', '2024-01-31', 'normal') AS total_horas;

SELECT 'Joao Silva' AS funcionario,
       calcular_horas(1, '2024-01-01', '2024-01-31', 'extras') AS horas_extras;

-- 6. PROCEDURE

CALL registrar_saida(1, '18:00');

-- 7. VIEWS

SELECT * FROM vw_registro_completo ORDER BY data DESC;
SELECT * FROM vw_atrasos ORDER BY data DESC;
