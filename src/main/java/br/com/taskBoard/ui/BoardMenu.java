package br.com.taskBoard.ui;

import br.com.taskBoard.dto.BoardColumnInfoDTO;
import br.com.taskBoard.persistence.entity.BoardColumnEntity;
import br.com.taskBoard.persistence.entity.BoardEntity;
import br.com.taskBoard.persistence.entity.CardEntity;
import br.com.taskBoard.service.BoardColumnQueryService;
import br.com.taskBoard.service.BoardQueryService;
import br.com.taskBoard.service.CardQueryService;
import br.com.taskBoard.service.CardService;
import lombok.AllArgsConstructor;

import javax.smartcardio.Card;
import java.sql.SQLException;
import java.util.Scanner;

import static br.com.taskBoard.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() {
        System.out.printf("\t\tBem vindo ao Board %s\n", entity.getName());
        System.out.println("\t selecione a operação desejada");
        var option = -1;
        try {
            while (option != 9) {
                System.out.println("---------------------------------------");
                System.out.println("\t1 - Ver board");
                System.out.println("\t2 - Ver colunas com cards");
                System.out.println("\t3 - Ver card");
                System.out.println("\t4 - Criar um novo card");
                System.out.println("\t5 - Mover um card");
                System.out.println("\t6 - Bloquear um card");
                System.out.println("\t7 - Desbloquear um card");
                System.out.println("\t8 - Cancelar um card");
                System.out.println("\t9 - Voltar ao menu anterior");
                System.out.println("\t0 - Sair");
                System.out.println("---------------------------------------");
                option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1 -> showBoard();
                    case 2 -> showColumn();
                    case 3 -> showCard();
                    case 4 -> creatCard();
                    case 5 -> moveCardToNextColumn();
                    case 6 -> blockCard();
                    case 7 -> unblockCard();
                    case 8 -> cancelCard();
                    case 9 -> System.out.println("Voltando ao menu anterior");
                    case 0 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void creatCard() throws SQLException, ClassNotFoundException {
        var card = new CardEntity();
        System.out.println("Informe o titulo do card");
        card.setTitle(scanner.nextLine());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.nextLine());
        card.setBoardColumn(entity.getInitialColumn());

        try(var connection = getConnection()) {
            new CardService(connection).creat(card);
            System.out.println("Card criado com sucesso!");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para próxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(),bc.getOrder(),bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId,entity.getId(),boardColumnsInfo);
            System.out.println("Card movido com sucesso");
            Thread.sleep(1000);
        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void blockCard() throws SQLException{
        System.out.println("informe o id do card que deseja bloquear");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("informe o motivo do bloqueado do card");
        var reason = scanner.nextLine();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(),bc.getOrder(),bc.getKind()))
                .toList();
        try (var connection = getConnection()){
            new CardService(connection).block(cardId,entity.getId(),reason,boardColumnsInfo);
            System.out.println("Card bloqueado com sucesso");
            Thread.sleep(1000);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void unblockCard() throws SQLException{
        System.out.println("informe o id do card que deseja desbloquear");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("informe o motivo do desbloqueado do card");
        var reason = scanner.nextLine();
        try (var connection = getConnection()){
            new CardService(connection).unblock(cardId,entity.getId(),reason);
            System.out.println("Card desbloqueado com sucesso");
            Thread.sleep(1000);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void cancelCard() throws SQLException{
        System.out.println("Informe o id do card que deseja cancelar");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getInitialColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(),bc.getOrder(),bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId,entity.getId(),cancelColumn.getId(),boardColumnsInfo);
            System.out.println("Card cancelado com sucesso");
            Thread.sleep(1000);
        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                        System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                        b.columnS().forEach(c ->
                                System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                        );
                    }
            );
        }

    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card: %s - %s\nDescrição: %s\n", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do carde que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId,entity.getId())
                    .ifPresentOrElse(c ->{
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ? "Está bloqueado. Motivo: " + c.blockReason() : "Não está bloqueado.");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }
}