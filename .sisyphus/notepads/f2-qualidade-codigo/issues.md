# Issues - Revisão de Qualidade de Código

## Issue 1: Wildcard Imports (Main.java)
- **Arquivo**: `src/br/com/ponto/Main.java`, linhas 3-4
- **Problema**: `import br.com.ponto.dao.*;` e `import br.com.ponto.model.*;`
- **Regra violada**: "Imports explícitos, sem wildcards"
- **Severidade**: Baixa — funcionalidade não afetada
- **Solução**: Substituir por imports explícitos de cada classe usada

## Issue 2: Código duplicado entre DAOs
- **Arquivos**: Todos os 5 DAOs (DepartamentoDAO, CargoDAO, FuncionarioDAO, RegistroPontoDAO, JustificativaDAO)
- **Problema**: Estrutura CRUD idêntica replicada em cada DAO
  - Padrão: try-with-resources → PreparedStatement → execute → process ResultSet
  - Apenas diferem: nome da tabela, colunas, classe modelo
- **Severidade**: Média — manutenção frágil, código boilerplate
- **Observação**: Comum em projetos JDBC simples sem ORM. Aceitável para escopo atual.
