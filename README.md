# Sistema de Ponto Eletrônico

Sistema acadêmico de controle de ponto eletrônico desenvolvido em Java com
banco de dados PostgreSQL. Possui interface interativa via console (menu)
para registro e consulta de batidas de ponto.

## Pré-requisitos

- **Java** 21 ou superior
- **PostgreSQL** 14+ instalado e rodando
- Utilitário `psql` para executar scripts SQL
- `wget` ou `curl` para baixar o driver JDBC

## Configuração do Banco de Dados

1. Crie o banco de dados:

   ```bash
   createdb ponto_eletronico
   ```

2. Execute os scripts SQL na ordem correta:

   ```bash
   psql -U paulo -d ponto_eletronico -f sql/01_ddl.sql
   psql -U paulo -d ponto_eletronico -f sql/02_dml.sql
   ```

   > O arquivo `01_ddl.sql` contém a definição das tabelas (DDL).
   > O arquivo `02_dml.sql` contém inserts de exemplo (DML), se houver.

## Compilação

Execute o script de setup para baixar automaticamente o driver JDBC do
PostgreSQL e compilar o projeto:

```bash
bash setup.sh
```

O driver será salvo em `lib/postgresql-42.7.3.jar` e os arquivos compilados
em `out/`.

## Execução

Após compilar, execute o sistema com:

```bash
java -cp out:lib/postgresql-42.7.3.jar br.com.ponto.Main
```

## Estrutura do Projeto

```
ponto-eletronico/
├── lib/                    # Dependências (driver JDBC)
├── out/                    # Arquivos .class compilados
├── sql/                    # Scripts SQL (DDL + DML)
├── src/                    # Código-fonte Java
│   └── br/com/ponto/
│       ├── config/         # Configurações do sistema
│       ├── connection/     # Conexão com o banco
│       ├── dao/            # Data Access Objects
│       └── model/          # Classes de modelo (entidades)
├── setup.sh                # Script de setup e compilação
├── README.md               # Este arquivo
└── .gitignore              # Arquivos ignorados pelo Git
```

## Licença

Projeto acadêmico — uso educacional.
