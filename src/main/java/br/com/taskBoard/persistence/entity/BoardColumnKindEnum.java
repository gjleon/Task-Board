package br.com.taskBoard.persistence.entity;

import java.util.Optional;
import java.util.stream.Stream;

public enum BoardColumnKindEnum {
    INITIAL, FINAL, CANCEL, PENDING;

public static BoardColumnKindEnum findByName(final String name) {
    return Stream.of(BoardColumnKindEnum.values())
            .filter(b -> b.name().equals(name))
            .findFirst().orElse(null);


}

}
