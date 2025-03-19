package br.com.taskBoard.service;

import br.com.taskBoard.persistence.dao.BoardColumnDAO;
import br.com.taskBoard.persistence.dao.BoardDAO;
import br.com.taskBoard.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {
    private final Connection connection;

    public BoardEntity insert(final BoardEntity boardEntity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try {
            dao.insert(boardEntity);
          var columns =  boardEntity.getBoardColumns().stream().map(c -> {
                c.setBoard(boardEntity);
                return c;
            }).toList();
          for (var column : columns) {
              boardColumnDAO.insert(column);
          }
            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            throw e;
        }
        return boardEntity;
    }

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if (!dao.exists(id)) {
                return false;
            }

            dao.delete(id);
            connection.commit();
            return true;
        }catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
