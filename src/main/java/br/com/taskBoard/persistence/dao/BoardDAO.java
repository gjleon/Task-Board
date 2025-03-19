package br.com.taskBoard.persistence.dao;

import br.com.taskBoard.persistence.entity.BoardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {
    private final Connection connection;

    public BoardEntity insert(final BoardEntity boardEntity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) VALUES (?)";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, boardEntity.getName());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                boardEntity.setId(impl.getLastInsertID());
            }
            return boardEntity;
        }
    }

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
        }

    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var entity = new BoardEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }

    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            return statement.getResultSet().next();
        }
    }

    public List<BoardEntity> findAll() throws SQLException {
        List<BoardEntity> boards = new ArrayList<>();
        var sql = "SELECT id, name FROM BOARDS";
        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var board = new BoardEntity();
                board.setId(resultSet.getLong("id"));
                board.setName(resultSet.getString("name"));
                boards.add(board);
            }

            return boards;
        }
    }
}
