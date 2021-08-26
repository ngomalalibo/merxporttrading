package com.merxport.trading.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base class for all persistable data
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Component
public class PersistingBaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @BsonProperty("_id")
    @JsonProperty("_id")
    private String id;
    private Audit audit = new Audit();
    private boolean isActive = true;
    
    public PersistingBaseEntity()
    {
        super();
    }
    
    public void auditLog()
    {
        if (Objects.isNull(this.getAudit().getCreatedDate()))
        {
            this.getAudit().setCreatedBy("System");
            this.getAudit().setCreatedDate(LocalDateTime.now());
        }
        else if (Objects.isNull(this.getAudit().getModifiedDate()))
        {
            this.getAudit().setModifiedBy("System");
            this.getAudit().setModifiedDate(LocalDateTime.now());
        }
    }
}
