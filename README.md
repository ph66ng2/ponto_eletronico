# 🕐 Sistema de Ponto Eletronico

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JDBC](https://img.shields.io/badge/JDBC-42.7.3-6B8E23?logo=postgresql)](https://jdbc.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> Projeto academico — Disciplina de Bancos de Dados I — UCSAL 2026

## 📋 Sobre

Sistema completo de controle de ponto eletronico desenvolvido como trabalho academico para a disciplina de **Bancos de Dados I** do curso de **Analise e Desenvolvimento de Sistemas** da **Universidade Catolica do Salvador (UCSAL)**.

O sistema gerencia o registro de ponto de funcionarios, permitindo cadastro de departamentos, cargos e funcionarios, registro de entradas e saidas, calculo de horas trabalhadas e gerenciamento de justificativas de ausencia.

**Stack:** Java puro + PostgreSQL + JDBC, sem frameworks ou ferramentas de build. Toda compilacao e execucao via linha de comando.

## 🚀 Quick Start

### Pre-requisitos

- **JDK 21+** no PATH
- **PostgreSQL 14+** em execucao
- **Bash** (Linux/macOS)

### 1. Iniciar PostgreSQL

```bash
pg_ctl -D /home/paulo/pgdata -l /home/paulo/pgdata/pg.log -o "-p 5433 -k /tmp" start
```

### 2. Criar banco (primeira vez)

```bash
createdb -h /tmp -p 5433 -U paulo ponto_eletronico
```

### 3. Executar script SQL

```bash
psql -h /tmp -p 5433 -U paulo -d ponto_eletronico -f sql/script.sql
```

### 4. Compilar

```bash
bash setup.sh
```

### 5. Executar

```bash
java -cp out:lib/postgresql-42.7.3.jar br.com.ponto.Main
```

## 🗄️ Estrutura do Banco

Banco `ponto_eletronico`, porta `5433`, usuario `paulo`, socket `/tmp`, autenticacao `trust`.

### Diagrama ER

```
┌──────────────┐         ┌──────────────┐
│ DEPARTAMENTO │ 1    N  │  FUNCIONARIO │
│   PK id      │────────▶│FK departamento│
│   nome       │         │FK cargo       │──────▶ CARGO
└──────────────┘         │   nome        │
                         │   cpf (UNIQUE)│
                         └───────┬───────┘
                  ┌──────────────┼──────────────┐
                  │ 1          N │ 1          N │
                  ▼              ▼              │
         ┌────────────┐  ┌──────────────────┐   │
         │REGISTRO    │  │JUSTIFICATIVA     │   │
         │_PONTO      │  │_AUSENCIA         │   │
         │PK id       │  │PK id             │   │
         │FK func     │  │FK func           │   │
         │data        │  │data              │   │
         │hora_entrada│  │tipo (atestado...)│   │
         │hora_saida  │  │aprovada          │   │
         │CHECK saida │  └──────────────────┘   │
         │  > entrada │                          │
         └────────────┘                          │
                                                 │
              CARGO ◀────────────────────────────┘
              PK id, titulo, salario_base (CHECK > 0)
```

### Tabelas

#### departamento

| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_departamento | SERIAL | PRIMARY KEY | Identificador unico autoincremento |
| nome | VARCHAR(100) | NOT NULL | Nome do departamento |
| descricao | TEXT | - | Descricao opcional |

#### cargo

| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_cargo | SERIAL | PRIMARY KEY | Identificador unico autoincremento |
| titulo | VARCHAR(100) | NOT NULL | Titulo do cargo |
| salario_base | NUMERIC(10,2) | NOT NULL, CHECK > 0 | Salario base |

#### funcionario

| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_funcionario | SERIAL | PRIMARY KEY | Identificador unico |
| nome | VARCHAR(150) | NOT NULL | Nome completo |
| cpf | VARCHAR(14) | UNIQUE, NOT NULL | CPF (unico) |
| id_departamento | INT | NOT NULL, FK -> departamento | Departamento |
| id_cargo | INT | NOT NULL, FK -> cargo | Cargo |
| data_admissao | DATE | NOT NULL, DEFAULT CURRENT_DATE | Data de admissao |

#### registro_ponto

| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_registro | SERIAL | PRIMARY KEY | Identificador unico |
| id_funcionario | INT | NOT NULL, FK -> funcionario | Funcionario |
| data | DATE | NOT NULL | Data do registro |
| hora_entrada | TIME | - | Hora de entrada |
| hora_saida | TIME | - | Hora de saida |
| observacao | TEXT | - | Observacao opcional |

**Constraint `chk_horario`:**
```sql
CHECK (hora_saida IS NULL OR hora_entrada IS NULL OR hora_saida > hora_entrada)
```

#### justificativa_ausencia

| Coluna | Tipo | Restricoes | Descricao |
|---|---|---|---|
| id_justificativa | SERIAL | PRIMARY KEY | Identificador unico |
| id_funcionario | INT | NOT NULL, FK -> funcionario | Funcionario |
| data | DATE | NOT NULL | Data da ausencia |
| tipo | VARCHAR(30) | NOT NULL, CHECK | Tipo da justificativa |
| descricao | TEXT | - | Descricao detalhada |
| aprovada | BOOLEAN | DEFAULT FALSE | Status de aprovacao |

**Tipos permitidos:** `atestado`, `ferias`, `pessoal`, `outro`

### View: vw_registro_completo

JOIN entre 4 tabelas (`registro_ponto`, `funcionario`, `departamento`, `cargo`) para consulta consolidada.

<details>
<summary>Ver definicao completa</summary>

```sql
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
```
</details>

### Function: calcular_horas

Calcula o total de horas trabalhadas por um funcionario em um periodo.

```sql
calcular_horas(p_id_funcionario INT, p_data_inicio DATE, p_data_fim DATE) RETURNS NUMERIC
```

<details>
<summary>Ver implementacao</summary>

```sql
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
```
</details>

**Exemplo de uso:**
```sql
SELECT calcular_horas(1, '2024-01-01', '2024-01-31') AS total_horas;
```

### Procedure: registrar_saida

Registra a hora de saida de um registro de ponto, com validacoes de existencia e ordem temporal.

```sql
registrar_saida(p_id_registro INT, p_hora_saida TIME)
```

<details>
<summary>Ver implementacao</summary>

```sql
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
```
</details>

**Exemplo de uso:**
```sql
CALL registrar_saida(1, '18:00');
```

## 🏗️ Arquitetura Java

13 arquivos Java em 4 pacotes.

### Arvore de Pacotes

```
src/br/com/ponto/
├── Main.java                          # Menu principal e interface
├── config/
│   └── DBConfig.java                  # Constantes de conexao
├── connection/
│   └── ConectaPostgres.java           # Gerenciamento de conexao JDBC
├── model/
│   ├── Departamento.java              # POJO - Departamento
│   ├── Cargo.java                     # POJO - Cargo
│   ├── Funcionario.java               # POJO - Funcionario
│   ├── RegistroPonto.java             # POJO - Registro de Ponto
│   └── JustificativaAusencia.java     # POJO - Justificativa
└── dao/
    ├── DepartamentoDAO.java           # CRUD - Departamento
    ├── CargoDAO.java                  # CRUD - Cargo
    ├── FuncionarioDAO.java            # CRUD - Funcionario
    ├── RegistroPontoDAO.java          # CRUD + Views + Functions + Procedures
    └── JustificativaDAO.java          # CRUD + Aprovacao
```

### Menu do Sistema

```
============================================
  SISTEMA DE PONTO ELETRONICO
============================================
1. Cadastros (Departamentos e Cargos)
2. Funcionarios
3. Ponto e Relatorios
0. Sair
```

<details>
<summary>Ver submenus completos</summary>

**Submenu Cadastros:**
- 1. Departamentos (Cadastrar, Listar, Buscar por ID, Atualizar, Remover)
- 2. Cargos (Cadastrar, Listar, Buscar por ID, Atualizar, Remover)

**Submenu Funcionarios:**
- 1. Cadastrar (com selecao de departamento e cargo)
- 2. Listar todos
- 3. Buscar por ID
- 4. Atualizar
- 5. Listar com Departamento e Cargo (JOIN)
- 6. Remover

**Submenu Ponto e Relatorios:**
- 1. Registrar entrada (data e hora, com padroes de hoje/agora)
- 2. Registrar saida (via PROCEDURE `registrar_saida`)
- 3. Listar registros de ponto
- 4. Registro completo (via VIEW `vw_registro_completo`)
- 5. Calcular horas trabalhadas (via FUNCTION `calcular_horas`)
- 6. Justificativas (Inserir, Listar, Buscar, Aprovar, Remover)
</details>

## 📊 Regras de Negocio

| Codigo | Regra | Implementacao |
|---|---|---|
| RN01 | Salario base positivo | CHECK (salario_base > 0) em `cargo` |
| RN02 | CPF unico | UNIQUE em `funcionario.cpf` |
| RN03 | Saida posterior a entrada | CHECK `chk_horario` + procedure `registrar_saida` |
| RN04 | Registros incompletos ignorados | `calcular_horas` filtra NULLs |
| RN05 | Vinculo obrigatorio depto/cargo | NOT NULL + FKs em `funcionario` |
| RN06 | Tipos de justificativa restritos | CHECK (tipo IN (...)) |
| RN07 | Aprovacao padrao = FALSE | DEFAULT FALSE em `justificativa_ausencia` |
| RN08 | Protecao de integridade referencial | FK constraints + tratamento SQLState 23503 |
| RN09 | Saida via procedure | `registrar_saida` valida antes de UPDATE |
| RN10 | Script SQL idempotente | DROP IF EXISTS antes de CREATE |

### Requisitos Funcionais (18)

<details>
<summary>Ver todos os RF01-RF18</summary>

| Codigo | Descricao |
|---|---|
| RF01 | Cadastrar departamentos |
| RF02 | Cadastrar cargos com salario base |
| RF03 | Cadastrar funcionarios com CPF unico |
| RF04 | Registrar entrada de ponto (data e hora) |
| RF05 | Registrar saida via PROCEDURE com validacao |
| RF06 | Listar todos os registros de ponto |
| RF07 | Consultar registro completo com JOIN (View) |
| RF08 | Calcular horas trabalhadas no periodo (Function) |
| RF09 | Cadastrar justificativas de ausencia |
| RF10 | Aprovar justificativas |
| RF11 | Listar funcionarios com departamento e cargo (JOIN) |
| RF12 | Validar CPF duplicado (UNIQUE constraint) |
| RF13 | Validar horario (saida > entrada) |
| RF14 | Atualizar dados de departamentos, cargos e funcionarios |
| RF15 | Remover registros com protecao de FK |
| RF16 | Buscar entidades por ID |
| RF17 | Listar justificativas com filtro de aprovacao |
| RF18 | Encerrar aplicacao com desconexao do banco |
</details>

### Requisitos Nao Funcionais (12)

<details>
<summary>Ver todos os RNF01-RNF12</summary>

| Codigo | Descricao |
|---|---|
| RNF01 | Desenvolvido em Java (JDK 21+) |
| RNF02 | PostgreSQL como banco de dados |
| RNF03 | JDBC para conexao (driver postgresql-42.7.3.jar) |
| RNF04 | Execucao via terminal (sem interface grafica) |
| RNF05 | Separacao de responsabilidades (model/dao/config/connection) |
| RNF06 | PreparedStatement em todas as queries |
| RNF07 | Tratamento de excecoes com mensagens em portugues |
| RNF08 | Integridade referencial com FKs |
| RNF09 | Script de setup automatizado (setup.sh) |
| RNF10 | Script SQL idempotente (DROP antes de CREATE) |
| RNF11 | Zero dependencias externas alem do driver JDBC |
| RNF12 | Codigo comentado em portugues |
</details>

## ✅ Suite de Testes

O arquivo `sql/script.sql` contem exemplos de operacoes que servem como validacao pratica:

- **Insercao:** 2 departamentos, 2 cargos, 2 funcionarios, 5 registros de ponto, 3 justificativas
- **CRUD:** UPDATE de justificativa e cargo, DELETE de justificativa e registro
- **JOINs:** Funcionarios com depto/cargo/salario, registros com dados completos, justificativas com dados do funcionario
- **Function:** `calcular_horas(1, '2024-01-01', '2024-01-31')`
- **Procedure:** `CALL registrar_saida(1, '18:00')`
- **View:** `SELECT * FROM vw_registro_completo`
- **Constraints:** CHECK salario > 0, UNIQUE CPF, CHECK horario, CHECK tipo

## 👥 Autores

Paulo Medeiros e
Nicolas Machado

**Orientadora:** Angela Peixoto

**Instituicao:** Universidade Catolica do Salvador (UCSAL) — 2026

## 📄 Licenca

Projeto academico desenvolvido para fins educacionais.
