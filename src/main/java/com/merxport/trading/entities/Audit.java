package com.merxport.trading.entities;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Audit implements Serializable
{
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime archivedDate;
    private String createdBy;
    private String modifiedBy;
    private String archivedBy;
}
