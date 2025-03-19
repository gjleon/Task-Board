package br.com.taskBoard.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static br.com.taskBoard.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.taskBoard.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class BoardEntity {
    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn() {
        return getFilteredCColumn(bc -> bc.getKind().equals(INITIAL));
    }

    public BoardColumnEntity getCardColumn(int column) {
        return getFilteredCColumn(bc -> bc.getKind().equals(CANCEL));
    }

    private BoardColumnEntity getFilteredCColumn(Predicate<BoardColumnEntity> filter){
        return boardColumns.stream()
                .filter(filter)
                .findFirst().orElseThrow();
    }
}
