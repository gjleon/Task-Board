package br.com.taskBoard.dto;

import java.util.List;

public record BoardDetailsDTO(
        Long id,
        String name,
        List<BoardColumnDTO> columnS
) {
}
