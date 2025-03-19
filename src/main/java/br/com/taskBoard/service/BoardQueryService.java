package br.com.taskBoard.service;

import br.com.taskBoard.dto.BoardDetailsDTO;
import br.com.taskBoard.persistence.dao.BoardColumnDAO;
import br.com.taskBoard.persistence.dao.BoardDAO;
import br.com.taskBoard.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()) {
            var boardEntity = optional.get();
            boardEntity.setBoardColumns(boardColumnDAO.findByBoardId(boardEntity.getId()));
            return Optional.of(boardEntity);
        }
        return Optional.empty();
    }
    public List<BoardEntity> findAll() throws SQLException {
        var dao = new BoardDAO(connection);
        return dao.findAll();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()) {
            var boardEntity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(boardEntity.getId());
            var dto = new BoardDetailsDTO(boardEntity.getId(), boardEntity.getName(),columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

}
