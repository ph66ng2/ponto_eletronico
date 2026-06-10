package br.com.ponto;

import br.com.ponto.dao.*;
import br.com.ponto.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final DepartamentoDAO deptDAO = new DepartamentoDAO();
    private static final CargoDAO cargoDAO = new CargoDAO();
    private static final FuncionarioDAO funcDAO = new FuncionarioDAO();
    private static final RegistroPontoDAO pontoDAO = new RegistroPontoDAO();
    private static final JustificativaDAO justDAO = new JustificativaDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        while (true) {
            exibirMenuPrincipal();
            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: menuCadastros(); break;
                case 2: menuFuncionarios(); break;
                case 3: menuPontoRelatorios(); break;
                case 0:
                    System.out.println("Encerrando...");
                    System.exit(0);
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // ======================================================================
    // Helpers de entrada (protegidos contra NumberFormatException)
    // ======================================================================

    private static int lerInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número.");
            }
        }
    }

    private static String lerString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static BigDecimal lerBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(sc.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um valor decimal (ex: 3500.00).");
            }
        }
    }

    private static LocalDate lerData(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (dd/MM/yyyy): ");
                return LocalDate.parse(sc.nextLine().trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Use o formato dd/MM/yyyy.");
            }
        }
    }

    private static LocalTime lerHora(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (HH:mm): ");
                return LocalTime.parse(sc.nextLine().trim(), TIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Hora inválida! Use o formato HH:mm.");
            }
        }
    }

    private static boolean lerBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String val = sc.nextLine().trim().toUpperCase();
            if ("S".equals(val)) return true;
            if ("N".equals(val)) return false;
            System.out.println("Responda S (sim) ou N (não).");
        }
    }

    private static void pausa() {
        System.out.print("\nPressione ENTER para continuar...");
        sc.nextLine();
    }

    // ======================================================================
    // Menu Principal
    // ======================================================================

    private static void exibirMenuPrincipal() {
        System.out.println();
        System.out.println("============================================");
        System.out.println("  SISTEMA DE PONTO ELETRÔNICO");
        System.out.println("============================================");
        System.out.println("1. Cadastros (Departamentos e Cargos)");
        System.out.println("2. Funcionários");
        System.out.println("3. Ponto e Relatórios");
        System.out.println("0. Sair");
    }

    // ======================================================================
    // Submenu 1: Cadastros (Departamentos + Cargos)
    // ======================================================================

    private static void menuCadastros() {
        while (true) {
            System.out.println();
            System.out.println("--- CADASTROS ---");
            System.out.println("1. Departamentos");
            System.out.println("2. Cargos");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: menuDepartamentos(); break;
                case 2: menuCargos(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    // ======================================================================
    // Submenu: Departamentos
    // ======================================================================

    private static void menuDepartamentos() {
        while (true) {
            System.out.println();
            System.out.println("--- DEPARTAMENTOS ---");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar todos");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Atualizar");
            System.out.println("5. Remover");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: cadastrarDepartamento(); break;
                case 2: listarDepartamentos(); break;
                case 3: buscarDepartamentoPorId(); break;
                case 4: atualizarDepartamento(); break;
                case 5: removerDepartamento(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarDepartamento() {
        String nome = lerString("Nome do departamento: ");
        if (nome.isEmpty()) {
            System.out.println("Nome não pode ser vazio.");
            return;
        }
        String descricao = lerString("Descrição: ");
        Departamento d = new Departamento();
        d.setNome(nome);
        d.setDescricao(descricao);
        deptDAO.inserir(d);
        pausa();
    }

    private static void listarDepartamentos() {
        List<Departamento> lista = deptDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum departamento cadastrado.");
        } else {
            System.out.println("\n=== Departamentos ===");
            System.out.printf("%-5s %-25s %-40s%n", "ID", "Nome", "Descrição");
            System.out.println("=".repeat(75));
            for (Departamento d : lista) {
                System.out.printf("%-5d %-25s %-40s%n",
                        d.getIdDepartamento(), d.getNome(),
                        d.getDescricao() != null ? d.getDescricao() : "");
            }
        }
        pausa();
    }

    private static void buscarDepartamentoPorId() {
        int id = lerInt("ID do departamento: ");
        Departamento d = deptDAO.buscarPorId(id);
        if (d == null) {
            System.out.println("Departamento não encontrado.");
        } else {
            System.out.println("ID: " + d.getIdDepartamento());
            System.out.println("Nome: " + d.getNome());
            System.out.println("Descrição: " + d.getDescricao());
        }
        pausa();
    }

    private static void atualizarDepartamento() {
        int id = lerInt("ID do departamento: ");
        Departamento d = deptDAO.buscarPorId(id);
        if (d == null) {
            System.out.println("Departamento não encontrado.");
            return;
        }
        System.out.println("Departamento atual: " + d.getNome() + " - " + d.getDescricao());
        String nome = lerString("Novo nome (ENTER para manter): ");
        if (!nome.isEmpty()) d.setNome(nome);
        String descricao = lerString("Nova descrição (ENTER para manter): ");
        if (!descricao.isEmpty()) d.setDescricao(descricao);
        deptDAO.atualizar(d);
        pausa();
    }

    private static void removerDepartamento() {
        int id = lerInt("ID do departamento: ");
        deptDAO.remover(id);
        pausa();
    }

    // ======================================================================
    // Submenu: Cargos
    // ======================================================================

    private static void menuCargos() {
        while (true) {
            System.out.println();
            System.out.println("--- CARGOS ---");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar todos");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Atualizar");
            System.out.println("5. Remover");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: cadastrarCargo(); break;
                case 2: listarCargos(); break;
                case 3: buscarCargoPorId(); break;
                case 4: atualizarCargo(); break;
                case 5: removerCargo(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarCargo() {
        String titulo = lerString("Título do cargo: ");
        if (titulo.isEmpty()) {
            System.out.println("Título não pode ser vazio.");
            return;
        }
        BigDecimal salarioBase = lerBigDecimal("Salário base: ");
        Cargo c = new Cargo();
        c.setTitulo(titulo);
        c.setSalarioBase(salarioBase);
        cargoDAO.inserir(c);
        pausa();
    }

    private static void listarCargos() {
        List<Cargo> lista = cargoDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cargo cadastrado.");
        } else {
            System.out.println("\n=== Cargos ===");
            System.out.printf("%-5s %-25s %-15s%n", "ID", "Título", "Salário Base");
            System.out.println("=".repeat(50));
            for (Cargo c : lista) {
                System.out.printf("%-5d %-25s %-15s%n",
                        c.getIdCargo(), c.getTitulo(),
                        c.getSalarioBase() != null ? c.getSalarioBase().toString() : "0.00");
            }
        }
        pausa();
    }

    private static void buscarCargoPorId() {
        int id = lerInt("ID do cargo: ");
        Cargo c = cargoDAO.buscarPorId(id);
        if (c == null) {
            System.out.println("Cargo não encontrado.");
        } else {
            System.out.println("ID: " + c.getIdCargo());
            System.out.println("Título: " + c.getTitulo());
            System.out.println("Salário Base: " + c.getSalarioBase());
        }
        pausa();
    }

    private static void atualizarCargo() {
        int id = lerInt("ID do cargo: ");
        Cargo c = cargoDAO.buscarPorId(id);
        if (c == null) {
            System.out.println("Cargo não encontrado.");
            return;
        }
        System.out.println("Cargo atual: " + c.getTitulo() + " - " + c.getSalarioBase());
        String titulo = lerString("Novo título (ENTER para manter): ");
        if (!titulo.isEmpty()) c.setTitulo(titulo);
        String salarioStr = lerString("Novo salário (ENTER para manter): ");
        if (!salarioStr.isEmpty()) {
            try {
                c.setSalarioBase(new BigDecimal(salarioStr.replace(",", ".")));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Mantendo salário atual.");
            }
        }
        cargoDAO.atualizar(c);
        pausa();
    }

    private static void removerCargo() {
        int id = lerInt("ID do cargo: ");
        cargoDAO.remover(id);
        pausa();
    }

    // ======================================================================
    // Submenu 2: Funcionários
    // ======================================================================

    private static void menuFuncionarios() {
        while (true) {
            System.out.println();
            System.out.println("--- FUNCIONÁRIOS ---");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar todos");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Atualizar");
            System.out.println("5. Listar com Departamento e Cargo (JOIN)");
            System.out.println("6. Remover");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: cadastrarFuncionario(); break;
                case 2: listarFuncionarios(); break;
                case 3: buscarFuncionarioPorId(); break;
                case 4: atualizarFuncionario(); break;
                case 5: funcDAO.listarComDepartamentoECargo(); pausa(); break;
                case 6: removerFuncionario(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarFuncionario() {
        String nome = lerString("Nome do funcionário: ");
        if (nome.isEmpty()) {
            System.out.println("Nome não pode ser vazio.");
            return;
        }
        String cpf = lerString("CPF: ");
        if (cpf.isEmpty()) {
            System.out.println("CPF não pode ser vazio.");
            return;
        }

        List<Departamento> depts = deptDAO.listarTodos();
        if (depts.isEmpty()) {
            System.out.println("Cadastre um departamento primeiro.");
            return;
        }
        System.out.println("\nDepartamentos disponíveis:");
        for (Departamento d : depts) {
            System.out.printf("  [%d] %s%n", d.getIdDepartamento(), d.getNome());
        }
        int idDept = lerInt("ID do departamento: ");

        List<Cargo> cargos = cargoDAO.listarTodos();
        if (cargos.isEmpty()) {
            System.out.println("Cadastre um cargo primeiro.");
            return;
        }
        System.out.println("\nCargos disponíveis:");
        for (Cargo c : cargos) {
            System.out.printf("  [%d] %s%n", c.getIdCargo(), c.getTitulo());
        }
        int idCargo = lerInt("ID do cargo: ");
        LocalDate dataAdmissao = lerData("Data de admissão");

        Funcionario f = new Funcionario();
        f.setNome(nome);
        f.setCpf(cpf);
        f.setIdDepartamento(idDept);
        f.setIdCargo(idCargo);
        f.setDataAdmissao(dataAdmissao);
        funcDAO.inserir(f);
        pausa();
    }

    private static void listarFuncionarios() {
        List<Funcionario> lista = funcDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum funcionário cadastrado.");
        } else {
            System.out.println("\n=== Funcionários ===");
            System.out.printf("%-5s %-25s %-15s %-15s %-15s %-15s%n",
                    "ID", "Nome", "CPF", "ID Depto", "ID Cargo", "Data Adm.");
            System.out.println("=".repeat(95));
            for (Funcionario f : lista) {
                System.out.printf("%-5d %-25s %-15s %-15d %-15d %-15s%n",
                        f.getIdFuncionario(), f.getNome(), f.getCpf(),
                        f.getIdDepartamento(), f.getIdCargo(),
                        f.getDataAdmissao() != null ? f.getDataAdmissao().format(DATE_FMT) : "-");
            }
        }
        pausa();
    }

    private static void buscarFuncionarioPorId() {
        int id = lerInt("ID do funcionário: ");
        Funcionario f = funcDAO.buscarPorId(id);
        if (f == null) {
            System.out.println("Funcionário não encontrado.");
        } else {
            System.out.println("ID: " + f.getIdFuncionario());
            System.out.println("Nome: " + f.getNome());
            System.out.println("CPF: " + f.getCpf());
            System.out.println("ID Departamento: " + f.getIdDepartamento());
            System.out.println("ID Cargo: " + f.getIdCargo());
            System.out.println("Data Admissão: " + (f.getDataAdmissao() != null ? f.getDataAdmissao().format(DATE_FMT) : "-"));
        }
        pausa();
    }

    private static void atualizarFuncionario() {
        int id = lerInt("ID do funcionário: ");
        Funcionario f = funcDAO.buscarPorId(id);
        if (f == null) {
            System.out.println("Funcionário não encontrado.");
            return;
        }
        System.out.println("Funcionário atual: " + f.getNome() + " - CPF: " + f.getCpf());
        String nome = lerString("Novo nome (ENTER para manter): ");
        if (!nome.isEmpty()) f.setNome(nome);
        String cpf = lerString("Novo CPF (ENTER para manter): ");
        if (!cpf.isEmpty()) f.setCpf(cpf);

        String idDeptStr = lerString("Novo ID do departamento (ENTER para manter): ");
        if (!idDeptStr.isEmpty()) {
            try { f.setIdDepartamento(Integer.parseInt(idDeptStr)); }
            catch (NumberFormatException e) { System.out.println("Valor inválido. Mantendo departamento atual."); }
        }
        String idCargoStr = lerString("Novo ID do cargo (ENTER para manter): ");
        if (!idCargoStr.isEmpty()) {
            try { f.setIdCargo(Integer.parseInt(idCargoStr)); }
            catch (NumberFormatException e) { System.out.println("Valor inválido. Mantendo cargo atual."); }
        }
        funcDAO.atualizar(f);
        pausa();
    }

    private static void removerFuncionario() {
        int id = lerInt("ID do funcionário: ");
        funcDAO.remover(id);
        pausa();
    }

    // ======================================================================
    // Submenu 3: Ponto e Relatórios
    // ======================================================================

    private static void menuPontoRelatorios() {
        while (true) {
            System.out.println();
            System.out.println("--- PONTO E RELATÓRIOS ---");
            System.out.println("1. Registrar entrada");
            System.out.println("2. Registrar saída (Procedure)");
            System.out.println("3. Listar registros de ponto");
            System.out.println("4. Registro completo (View)");
            System.out.println("5. Atrasos (View)");
            System.out.println("6. Calcular horas trabalhadas (Function - normal)");
            System.out.println("7. Calcular horas extras (Function - modo extras)");
            System.out.println("8. Justificativas (CRUD)");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: registrarEntrada(); break;
                case 2: registrarSaida(); break;
                case 3: listarTodosRegistros(); break;
                case 4: pontoDAO.listarRegistroCompleto(); pausa(); break;
                case 5: pontoDAO.listarAtrasos(); pausa(); break;
                case 6: calcularHorasNormal(); break;
                case 7: calcularHorasExtras(); break;
                case 8: menuJustificativas(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    // --- Operações de Ponto ---

    private static void registrarEntrada() {
        int idFunc = lerInt("ID do funcionário: ");
        String dataStr = lerString("Data do registro (dd/MM/yyyy, ENTER para hoje): ");
        LocalDate data;
        if (dataStr.isEmpty()) {
            data = LocalDate.now();
        } else {
            try {
                data = LocalDate.parse(dataStr, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Usando data de hoje.");
                data = LocalDate.now();
            }
        }
        String entradaStr = lerString("Hora de entrada (HH:mm, ENTER para agora): ");
        LocalTime horaEntrada;
        if (entradaStr.isEmpty()) {
            horaEntrada = LocalTime.now();
        } else {
            try {
                horaEntrada = LocalTime.parse(entradaStr, TIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Hora inválida. Usando hora atual.");
                horaEntrada = LocalTime.now();
            }
        }
        String observacao = lerString("Observação (opcional): ");

        RegistroPonto r = new RegistroPonto();
        r.setIdFuncionario(idFunc);
        r.setData(data);
        r.setHoraEntrada(horaEntrada);
        r.setObservacao(observacao);
        pontoDAO.inserir(r);
        pausa();
    }

    private static void registrarSaida() {
        int idRegistro = lerInt("ID do registro de ponto: ");
        String horaSaida = lerString("Hora de saída (HH:mm): ");
        if (horaSaida.isEmpty()) {
            System.out.println("Hora de saída é obrigatória.");
            return;
        }
        pontoDAO.registrarSaida(idRegistro, horaSaida);
        pausa();
    }

    private static void listarTodosRegistros() {
        List<RegistroPonto> lista = pontoDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum registro de ponto encontrado.");
        } else {
            System.out.println("\n=== Registros de Ponto ===");
            System.out.printf("%-5s %-10s %-15s %-10s %-10s %-20s%n",
                    "ID", "ID Func", "Data", "Entrada", "Saída", "Observação");
            System.out.println("=".repeat(80));
            for (RegistroPonto r : lista) {
                System.out.printf("%-5d %-10d %-15s %-10s %-10s %-20s%n",
                        r.getIdRegistro(),
                        r.getIdFuncionario(),
                        r.getData() != null ? r.getData().format(DATE_FMT) : "-",
                        r.getHoraEntrada() != null ? r.getHoraEntrada().format(TIME_FMT) : "-",
                        r.getHoraSaida() != null ? r.getHoraSaida().format(TIME_FMT) : "-",
                        r.getObservacao() != null ? r.getObservacao() : "");
            }
        }
        pausa();
    }

    // --- Funções de Cálculo (unificadas) ---

    private static void calcularHorasNormal() {
        int idFunc = lerInt("ID do funcionário: ");
        String dataInicio = lerString("Data início (yyyy-MM-dd): ");
        String dataFim = lerString("Data fim (yyyy-MM-dd): ");
        pontoDAO.calcularHoras(idFunc, dataInicio, dataFim, "normal");
        pausa();
    }

    private static void calcularHorasExtras() {
        int idFunc = lerInt("ID do funcionário: ");
        String dataInicio = lerString("Data início (yyyy-MM-dd): ");
        String dataFim = lerString("Data fim (yyyy-MM-dd): ");
        pontoDAO.calcularHoras(idFunc, dataInicio, dataFim, "extras");
        pausa();
    }

    // ======================================================================
    // Submenu: Justificativas (via DAO, sem Procedure)
    // ======================================================================

    private static void menuJustificativas() {
        while (true) {
            System.out.println();
            System.out.println("--- JUSTIFICATIVAS ---");
            System.out.println("1. Inserir");
            System.out.println("2. Listar todas");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Aprovar");
            System.out.println("5. Remover");
            System.out.println("0. Voltar");

            int op = lerInt("Escolha: ");
            switch (op) {
                case 1: inserirJustificativa(); break;
                case 2: listarJustificativas(); break;
                case 3: buscarJustificativaPorId(); break;
                case 4: aprovarJustificativa(); break;
                case 5: removerJustificativa(); break;
                case 0: return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void inserirJustificativa() {
        int idFunc = lerInt("ID do funcionário: ");
        String dataStr = lerString("Data da justificativa (dd/MM/yyyy, ENTER para hoje): ");
        LocalDate data;
        if (dataStr.isEmpty()) {
            data = LocalDate.now();
        } else {
            try {
                data = LocalDate.parse(dataStr, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Usando data de hoje.");
                data = LocalDate.now();
            }
        }
        System.out.print("Tipo (atestado, ferias, pessoal, outro): ");
        String tipo = sc.nextLine().trim().toLowerCase();
        if (tipo.isEmpty()) tipo = "outro";
        String descricao = lerString("Descrição: ");

        JustificativaAusencia j = new JustificativaAusencia();
        j.setIdFuncionario(idFunc);
        j.setData(data);
        j.setTipo(tipo);
        j.setDescricao(descricao);
        j.setAprovada(false);
        justDAO.inserir(j);
        pausa();
    }

    private static void listarJustificativas() {
        List<JustificativaAusencia> lista = justDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma justificativa cadastrada.");
        } else {
            System.out.println("\n=== Justificativas de Ausência ===");
            System.out.printf("%-5s %-10s %-15s %-12s %-12s %-30s%n",
                    "ID", "ID Func", "Data", "Tipo", "Aprovada", "Descrição");
            System.out.println("=".repeat(95));
            for (JustificativaAusencia j : lista) {
                System.out.printf("%-5d %-10d %-15s %-12s %-12s %-30s%n",
                        j.getIdJustificativa(),
                        j.getIdFuncionario(),
                        j.getData() != null ? j.getData().format(DATE_FMT) : "-",
                        j.getTipo(),
                        j.isAprovada() ? "Sim" : "Não",
                        j.getDescricao() != null ? j.getDescricao() : "");
            }
        }
        pausa();
    }

    private static void buscarJustificativaPorId() {
        int id = lerInt("ID da justificativa: ");
        JustificativaAusencia j = justDAO.buscarPorId(id);
        if (j == null) {
            System.out.println("Justificativa não encontrada.");
        } else {
            System.out.println("ID: " + j.getIdJustificativa());
            System.out.println("ID Funcionário: " + j.getIdFuncionario());
            System.out.println("Data: " + (j.getData() != null ? j.getData().format(DATE_FMT) : "-"));
            System.out.println("Tipo: " + j.getTipo());
            System.out.println("Descrição: " + j.getDescricao());
            System.out.println("Aprovada: " + (j.isAprovada() ? "Sim" : "Não"));
        }
        pausa();
    }

    private static void aprovarJustificativa() {
        int id = lerInt("ID da justificativa: ");
        justDAO.aprovar(id);
        pausa();
    }

    private static void removerJustificativa() {
        int id = lerInt("ID da justificativa: ");
        justDAO.remover(id);
        pausa();
    }

}
