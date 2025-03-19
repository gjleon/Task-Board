package br.com.taskBoard.service;

import br.com.taskBoard.dto.CardDetailsDTO;
import br.com.taskBoard.persistence.dao.CardDAO;
import br.com.taskBoard.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final long cardId, final long boardId) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(cardId,boardId);
    }
}
