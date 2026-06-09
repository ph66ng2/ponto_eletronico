-- ============================================================================
-- Script DML - Sistema de Ponto Eletrônico
-- Criado em: 09/06/2026
-- Descrição: Dados de exemplo e operações CRUD completas.
--            Inclui INSERTs realistas, UPDATEs, DELETEs seguros,
--            SELECTs com JOIN, chamadas a Functions, Procedures e Views.
-- Banco: PostgreSQL 14+
-- Dependência: 01_ddl.sql já deve ter sido executado.
-- Executar com: psql -U paulo -d ponto_eletronico -f sql/02_dml.sql
-- ============================================================================

-- ============================================================================
-- 1. INSERTs — Dados realistas em português (≥10 por tabela)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1.1. departamento (10 registros)
-- ----------------------------------------------------------------------------
INSERT INTO departamento (nome, descricao) VALUES
('Tecnologia da Informação', 'Setor responsável por infraestrutura e sistemas'),
('Recursos Humanos', 'Gestão de pessoas, recrutamento e benefícios'),
('Financeiro', 'Contabilidade, contas a pagar e receber'),
('Marketing', 'Publicidade, comunicação e relação com clientes'),
('Vendas', 'Equipe comercial e negociação com clientes'),
('Jurídico', 'Assessoria legal e contratos'),
('Operações', 'Logística e processos operacionais'),
('Qualidade', 'Controle de qualidade e certificações'),
('Pesquisa e Desenvolvimento', 'Inovação e novos produtos'),
('Suporte ao Cliente', 'Central de atendimento ao consumidor');

-- ----------------------------------------------------------------------------
-- 1.2. cargo (12 registros)
-- ----------------------------------------------------------------------------
INSERT INTO cargo (titulo, salario_base) VALUES
('Analista de Sistemas', 7500.00),
('Gerente de TI', 12000.00),
('Assistente de RH', 3200.00),
('Coordenador Financeiro', 8500.00),
('Analista de Marketing', 5500.00),
('Diretor Comercial', 15000.00),
('Advogado Junior', 6500.00),
('Operador Logístico', 2800.00),
('Analista de Qualidade', 4800.00),
('Pesquisador Senior', 11000.00),
('Atendente de SAC', 2200.00),
('Auxiliar Administrativo', 2500.00);

-- ----------------------------------------------------------------------------
-- 1.3. funcionario (12 registros)
-- ----------------------------------------------------------------------------
INSERT INTO funcionario (nome, cpf, id_departamento, id_cargo, data_admissao) VALUES
('João Silva',        '111.222.333-44', 1,  1,  '2023-03-15'),
('Maria Oliveira',    '222.333.444-55', 1,  2,  '2022-01-10'),
('Carlos Santos',     '333.444.555-66', 2,  3,  '2024-01-20'),
('Ana Costa',         '444.555.666-77', 3,  4,  '2023-06-01'),
('Pedro Lima',        '555.666.777-88', 4,  5,  '2022-11-15'),
('Lucia Ferreira',    '666.777.888-99', 5,  6,  '2021-08-01'),
('Rafael Souza',      '777.888.999-00', 6,  7,  '2023-02-10'),
('Juliana Rocha',     '888.999.000-11', 7,  8,  '2024-03-01'),
('Felipe Alves',      '999.000.111-22', 8,  9,  '2022-07-20'),
('Patricia Nunes',    '000.111.222-33', 9,  10, '2021-05-15'),
('Roberto Dias',      '123.456.789-01', 10, 11, '2023-09-01'),
('Camila Moraes',     '234.567.890-12', 1,  12, '2024-02-15');

-- ----------------------------------------------------------------------------
-- 1.4. registro_ponto (30 registros)
-- Mix de dias normais, atrasos, horas extras, registros em andamento,
-- meio período, distribuídos entre id_funcionario 1 a 5.
-- Período: 02/01/2024 a 15/01/2024 (dias úteis de janeiro/2024).
-- ----------------------------------------------------------------------------
INSERT INTO registro_ponto (id_funcionario, data, hora_entrada, hora_saida, observacao) VALUES
-- ── João Silva (id=1) — Analista de Sistemas / TI ────────────────────
(1, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(1, '2024-01-03', '08:15', '17:30', 'Chegou atrasado devido ao trânsito na Marginal'),
(1, '2024-01-04', '08:00', '19:00', 'Hora extra para deploy do sistema de folha'),
(1, '2024-01-05', '08:00', NULL,   'Em andamento - aguardando saída'),
(1, '2024-01-08', '07:30', '17:30', 'Chegou mais cedo para manutenção do servidor'),
(1, '2024-01-09', '08:00', '17:00', 'Dia normal'),
(1, '2024-01-10', '08:05', '17:00', 'Pequeno atraso de 5 minutos'),
(1, '2024-01-15', '08:30', '17:00', 'Chegou atrasado devido à chuva forte'),

-- ── Maria Oliveira (id=2) — Gerente de TI / TI ──────────────────────
(2, '2024-01-02', '07:55', '17:00', 'Dia normal'),
(2, '2024-01-03', '08:00', '17:00', 'Dia normal'),
(2, '2024-01-04', '08:00', '17:05', 'Dia normal'),
(2, '2024-01-05', '08:10', '17:00', 'Pequeno atraso por conta de reunião externa'),
(2, '2024-01-08', '08:00', '17:00', 'Dia normal'),
(2, '2024-01-09', '08:00', '17:00', 'Dia normal'),
(2, '2024-01-10', '08:00', '13:00', 'Meio período - consulta médica agendada'),

-- ── Carlos Santos (id=3) — Assistente de RH / RH ────────────────────
(3, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(3, '2024-01-03', '09:00', '17:00', 'Forte chuva causou atraso de 1 hora'),
(3, '2024-01-04', '08:00', '17:00', 'Dia normal'),
(3, '2024-01-05', '08:00', '17:00', 'Dia normal'),
(3, '2024-01-08', '08:20', '17:00', 'Atraso devido a problemas no transporte público'),

-- ── Ana Costa (id=4) — Coordenadora Financeira / Financeiro ─────────
(4, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(4, '2024-01-03', '08:30', '17:00', 'Atraso por reunião externa no banco'),
(4, '2024-01-04', '08:00', '17:00', 'Dia normal'),
(4, '2024-01-05', '08:00', '17:00', 'Dia normal'),
(4, '2024-01-08', '08:00', '17:00', 'Dia normal'),

-- ── Pedro Lima (id=5) — Analista de Marketing / Marketing ───────────
(5, '2024-01-02', '08:00', '17:00', 'Dia normal'),
(5, '2024-01-03', '08:00', '17:00', 'Dia normal'),
(5, '2024-01-04', '07:00', '19:00', 'Plantão para fechamento da campanha trimestral'),
(5, '2024-01-05', '08:00', '13:00', 'Meio expediente - aniversário do funcionário'),
(5, '2024-01-08', '08:00', '17:00', 'Registro a ser removido posteriormente');

-- ----------------------------------------------------------------------------
-- 1.5. justificativa_ausencia (12 registros)
-- Diferentes tipos: atestado, ferias, pessoal, outro.
-- Mix de aprovada=TRUE e aprovada=FALSE.
-- Distribuídos entre vários funcionários e datas.
-- ----------------------------------------------------------------------------
INSERT INTO justificativa_ausencia (id_funcionario, data, tipo, descricao, aprovada) VALUES
(1, '2024-01-15', 'atestado',  'Consulta médica de rotina',                    FALSE),
(2, '2024-01-10', 'ferias',    'Férias programadas de 10 dias',                 TRUE),
(3, '2024-01-12', 'pessoal',   'Problemas familiares urgentes',                 FALSE),
(4, '2024-01-08', 'atestado',  'Atestado médico - quadro de gripe',             TRUE),
(5, '2024-01-17', 'pessoal',   'Resolução de pendências bancárias',             FALSE),
(1, '2024-01-22', 'ferias',    'Férias de verão',                               TRUE),
(2, '2024-01-25', 'outro',     'Treinamento externo de liderança',              TRUE),
(3, '2024-01-29', 'atestado',  'Exame médico periódico',                        FALSE),
(4, '2024-01-18', 'pessoal',   'Casamento de familiar próximo',                 TRUE),
(5, '2024-01-30', 'outro',     'Viagem não autorizada',                         FALSE),
(6, '2024-01-26', 'pessoal',   'Mudança de residência',                         FALSE),
(8, '2024-01-31', 'atestado',  'Lesão por esforço repetitivo - 5 dias de afastamento', TRUE);


-- ============================================================================
-- 2. UPDATEs (3+)
-- ============================================================================

-- 2.1. Aprovar a justificativa do João (consulta médica)
UPDATE justificativa_ausencia
SET aprovada = TRUE
WHERE id_justificativa = 1;

-- 2.2. Reajustar salário do cargo de Gerente de TI
UPDATE cargo
SET salario_base = 13000.00
WHERE id_cargo = 2;

-- 2.3. Transferir Carlos Santos do RH para Pesquisa e Desenvolvimento
UPDATE funcionario
SET id_departamento = 9
WHERE id_funcionario = 3;


-- ============================================================================
-- 3. DELETEs (2+) — operações seguras, sem violar FK
-- ============================================================================

-- 3.1. Remover justificativa pendente (não aprovada) — sem FK filha
DELETE FROM justificativa_ausencia
WHERE aprovada = FALSE AND id_justificativa = 10;

-- 3.2. Remover registro de ponto — sem FK filha
DELETE FROM registro_ponto
WHERE id_registro = 30;


-- ============================================================================
-- 4. SELECTs com JOIN (3+) — consultas analíticas com 2+ tabelas
-- ============================================================================

-- 4.1. Funcionários com departamento e cargo (3 tabelas)
SELECT f.nome                         AS funcionario,
       f.cpf,
       d.nome                         AS departamento,
       c.titulo                       AS cargo,
       c.salario_base,
       f.data_admissao
FROM funcionario f
JOIN departamento d ON f.id_departamento = d.id_departamento
JOIN cargo c        ON f.id_cargo        = c.id_cargo
ORDER BY f.nome;

-- 4.2. Registros de ponto completos no período (4 tabelas)
SELECT f.nome          AS funcionario,
       d.nome          AS departamento,
       c.titulo        AS cargo,
       rp.data,
       rp.hora_entrada,
       rp.hora_saida,
       rp.observacao
FROM registro_ponto rp
JOIN funcionario f   ON rp.id_funcionario  = f.id_funcionario
JOIN departamento d  ON f.id_departamento  = d.id_departamento
JOIN cargo c         ON f.id_cargo         = c.id_cargo
WHERE rp.data BETWEEN '2024-01-01' AND '2024-01-31'
ORDER BY rp.data, f.nome;

-- 4.3. Justificativas com dados do funcionário (3 tabelas)
SELECT f.nome       AS funcionario,
       c.titulo     AS cargo,
       d.nome       AS departamento,
       ja.tipo,
       ja.descricao,
       ja.data,
       ja.aprovada
FROM justificativa_ausencia ja
JOIN funcionario f   ON ja.id_funcionario  = f.id_funcionario
JOIN cargo c         ON f.id_cargo         = c.id_cargo
JOIN departamento d  ON f.id_departamento  = d.id_departamento
ORDER BY ja.data DESC;


-- ============================================================================
-- 5. SELECTs chamando Functions (3)
-- ============================================================================

-- 5.1. Total de horas trabalhadas por João Silva em janeiro/2024
SELECT 'João Silva'                                                  AS funcionario,
       calcular_horas_trabalhadas(1, '2024-01-01'::date, '2024-01-31'::date) AS total_horas;

-- 5.2. Total de horas extras de João Silva em janeiro/2024
SELECT 'João Silva'                                                  AS funcionario,
       calcular_horas_extras(1, '2024-01-01'::date, '2024-01-31'::date)     AS horas_extras;

-- 5.3. Minutos de atraso de João Silva em 15/01/2024
SELECT 'João Silva'                                                  AS funcionario,
       '2024-01-15'                                                  AS data,
       calcular_minutos_atraso(1, '2024-01-15'::date)               AS minutos_atraso;


-- ============================================================================
-- 6. CALL de Procedures (3)
-- ============================================================================

-- 6.1. Registrar saída do João Silva no registro em andamento (id_registro=5)
CALL registrar_saida(5, '18:00'::time);

-- 6.2. Registrar justificativa de ausência para Maria Oliveira
CALL justificar_ausencia(2, '2024-01-20', 'atestado', 'Consulta médica de rotina');

-- 6.3. Emitir relatório de fechamento do mês para o departamento de TI —
--      janeiro/2024 (mês=1, ano=2024)
CALL fechar_mes(1, 1, 2024);


-- ============================================================================
-- 7. SELECT nas Views (3)
-- ============================================================================

-- 7.1. Visão completa dos registros de ponto
SELECT * FROM vw_registro_completo
ORDER BY data DESC
LIMIT 10;

-- 7.2. Lista de atrasos (entrada após 08:00)
SELECT * FROM vw_atrasos
ORDER BY data DESC
LIMIT 10;

-- 7.3. Total de horas extras por funcionário/mês
SELECT * FROM vw_horas_extras_mes
LIMIT 10;
