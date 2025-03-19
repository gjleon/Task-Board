package br.com.taskBoard.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.taskBoard.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {
    private final Connection connection;
   
    public void block(final long cardId, final String reason) {
        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) values (?, ?, ?)";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i++,toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unblock(long cardId, String reason) {
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblocked_at IS NULL";
        try (var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i++,toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
