# Learnings - Revisão de Qualidade de Código

## Checklist passou (10/12)
- Compilação: exit 0 — sem erros
- PreparedStatement: 100% das queries (Statement.RETURN_GENERATED_KEYS é constante, não query)
- try-with-resources: 100% das operações JDBC cobrem Connection, PreparedStatement, CallableStatement, ResultSet
- Scanner: apenas nextLine() + Integer.parseInt() com try-catch (zero nextInt)
- Mensagens: todas em português
- Nomes de variáveis: bem nomeadas (f, c, d, r, j aceitáveis em escopo local)
- CallableStatement vs SELECT: Procedures usam CallableStatement, Functions usam SELECT — correto
- BigDecimal: todos valores monetários (salarioBase, horas_trabalhadas, horas_extras)
- LocalDate/LocalTime: datas/horas 100% no java.time, sem java.util.Date

## Checklist falhou (2/12)
- Wildcard imports em Main.java (2 imports com *)
- Código duplicado entre DAOs (padrão CRUD replicado 5x)

## Slop detection
- Sem comentários excessivos (apenas Javadoc pontual)
- Sem abstração desnecessária (sem interfaces, factories, patterns desnecessários)
- Sem nomes genéricos problemáticos (x, temp, data, obj ausentes)
- Código duplicado entre DAOs é o único slop significativo
