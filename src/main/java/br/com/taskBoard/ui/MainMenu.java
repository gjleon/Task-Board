package br.com.taskBoard.ui;

import br.com.taskBoard.persistence.entity.BoardColumnEntity;
import br.com.taskBoard.persistence.entity.BoardColumnKindEnum;
import br.com.taskBoard.persistence.entity.BoardEntity;
import br.com.taskBoard.service.BoardQueryService;
import br.com.taskBoard.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.taskBoard.persistence.config.ConnectionConfig.getConnection;
import static br.com.taskBoard.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("\t\t\tBOAS VINDAS");
        System.out.println("\tEscolha uma das opções abaixo");
        var option = -1;
        while (true) {
            System.out.println("---------------------------------------");
            System.out.println("\t1 - Criar um novo board");
            System.out.println("\t2 - Exibir todos os boards");
            System.out.println("\t3 - Selecionar um board existente");
            System.out.println("\t4 - Excluir um board");
            System.out.println("\t0 - Sair");
            System.out.println("---------------------------------------");
            option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> showAllBoards();
                case 3 -> selectBoard();
                case 4 -> deleteBoard();
                case 0 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void showAllBoards() throws SQLException {
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var boards = queryService.findAll();
            boards.forEach(board -> System.out.printf("id: %d, name: %s\n", board.getId(), board.getName()));
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Digite o nome do seu board: ");
        entity.setName(scanner.nextLine());
        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        scanner.nextLine();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.nextLine();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final do board");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);

        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);

        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(b -> new BoardMenu(b).execute(),
                    () ->System.out.printf("Não foi encontrado um board com id %s \n", id));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("informe o id do board que deseja excluir");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluido com sucesso\n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s \n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
