package br.com.taskBoard.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(
        Long id,
        String title,
        String description,
        boolean blocked,
        OffsetDateTime blackedAt,
        String blockReason,
        int blocksAmount,
        long columnId,
        String columnName
) {
}
