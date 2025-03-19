package br.com.taskBoard.service;

import br.com.taskBoard.dto.BoardColumnInfoDTO;
import br.com.taskBoard.dto.CardDetailsDTO;
import br.com.taskBoard.exception.CardBlockException;
import br.com.taskBoard.exception.CardFinishedException;
import br.com.taskBoard.exception.EntityNotFoundException;
import br.com.taskBoard.persistence.dao.BlockDAO;
import br.com.taskBoard.persistence.dao.CardDAO;
import br.com.taskBoard.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static br.com.taskBoard.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity creat(final CardEntity entity) throws SQLException, ClassNotFoundException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final long cardId, final long boardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId, boardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            var nextColumn = checkIfCanBeMoved(boardColumnsInfo, dto);
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final long cardId, final long boardId, final long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId, boardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            var nextColumn = checkIfCanBeMoved(boardColumnsInfo, dto);
            dao.moveToColumn(nextColumn.id(), cardId);
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final long cardId, final long boardId, final String reason, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId, boardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            checkIfCanBeMoved(boardColumnsInfo, dto);
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(cardId, reason);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final long cardId, final long boardId, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId, boardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            if (!dto.blocked()) {
                throw new CardBlockException("Card de id %s não está bloqueado".formatted(dto.id()));
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(cardId, reason);
            connection.commit();
        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private static BoardColumnInfoDTO checkIfCanBeMoved(List<BoardColumnInfoDTO> boardColumnsInfo, CardDetailsDTO dto) {
        if (dto.blocked()) {
            throw new CardBlockException("Card de id %s está bloqueado, necessário desbloqueia-lo para fazer qualquer tipo de operação".formatted(dto.id()));
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card já foi finalizado");
        }
        return boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O cards esta cancelado"));
    }

}
