package br.com.taskBoard.dto;

import br.com.taskBoard.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(
        Long id,
        int order,
        BoardColumnKindEnum kind
) {
}
