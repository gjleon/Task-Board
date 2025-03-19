package br.com.taskBoard.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {
    private Long id;
    private OffsetDateTime blockDate;
    private String blockReason;
    private OffsetDateTime unblockDate;
    private String unblockReason;

}
