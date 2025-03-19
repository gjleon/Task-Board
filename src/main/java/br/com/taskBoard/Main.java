package br.com.taskBoard;

import br.com.taskBoard.persistence.migration.MigrationStrategy;
import br.com.taskBoard.ui.MainMenu;

import java.sql.SQLException;

import static br.com.taskBoard.persistence.config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}