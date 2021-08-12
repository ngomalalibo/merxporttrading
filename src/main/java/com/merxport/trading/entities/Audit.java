package com.merxport.trading.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Audit
{
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime deletedDate;
    private LocalDateTime archivedDate;
    private String createdBy;
    private String modifiedBy;
    private String archivedBy;
}
