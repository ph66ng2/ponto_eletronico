1: # Learnings - Sistema Ponto Eletrônico
2: 
3: ## Task 2: Script DDL
4: 
5: ### PostgreSQL local setup
6: - PostgreSQL 18.3 installed via system packages, data dir at `/var/lib/pgsql/data`
7: - System cluster runs on port 5432 — requires auth, no known password for user `paulo`
8: - Workaround: initialized local cluster at `/home/paulo/pgdata` on port 5433
9: - `unix_socket_directories` must be set to `/tmp` to avoid permission errors on `/var/run/postgresql/`
10: - Local cluster uses trust auth — simplifies dev workflow
11: 
12: ### DDL Script patterns
13: - DROP IF EXISTS before CREATE ensures idempotency
14: - Script runs cleanly on both first run and re-runs
15: - CREATE OR REPLACE for functions, views, and procedures enables safe redefinition
16: - Dependency order matters: tables → functions → views → procedures (reverse for DROPs)
17: 
18: ### Constraint validation
19: - `chk_horario` CHECK constraint correctly prevents `hora_saida <= hora_entrada`
20: - PostgreSQL error messages are descriptive: "viola a restrição de verificação 'chk_horario'"
21: 
22: ### Views referencing functions
23: - `vw_horas_extras_mes` calls `calcular_horas_extras()` — which already sums across records
24: - The SUM() aggregation over the function call may overcount for the same funcionario/month
25: - This is a known pattern issue but was implemented as specified in the plan
26: 
27: ## Task 4: Classes Model + DBConfig + DBConnection
28: 
29: ### Package structure
30: - `br.com.ponto.config` — DBConfig (constantes de conexão)
31: - `br.com.ponto.connection` — DBConnection (factory getConnection())
32: - `br.com.ponto.model` — 5 POJOs: Departamento, Cargo, Funcionario, RegistroPonto, JustificativaAusencia
33: 
34: ### POJO patterns
35: - DDL uses snake_case (id_departamento, salario_base), POJOs use camelCase (idDepartamento, salarioBase)
36: - Mapping será feito na camada DAO (T5-T7)
37: - Compilação com javac 21: sem erros, sem warnings
38: - Driver JDBC `lib/postgresql-42.7.3.jar` necessário no classpath apenas para DBConnection (importa java.sql)
39: 
40: ## Task 5: DAOs (DepartamentoDAO + CargoDAO)
41: 
42: ### Package structure
43: - `br.com.ponto.dao` — camada de acesso a dados
44: - JDBC jar necessário no classpath para DAOs também (importam java.sql)
45: 
46: ### DAO patterns
47: - Todos os métodos usam PreparedStatement (sem concatenação de strings SQL)
48: - Try-with-resources para Connection, PreparedStatement, ResultSet
49: - `Statement.RETURN_GENERATED_KEYS` nos INSERTs para capturar ID gerado
50: - Mensagens de erro e sucesso em português via System.out/System.err
51: - `setBigDecimal()` usado para mapear `salario_base` (BigDecimal) em CargoDAO
52: - `rs.getBigDecimal("salario_base")` para leitura do campo decimal
53: - Compilação: `javac -cp "lib/postgresql-42.7.3.jar:out" -d out src/br/com/ponto/dao/*.java`
54: - Ambos compilaram sem erros nem warnings
