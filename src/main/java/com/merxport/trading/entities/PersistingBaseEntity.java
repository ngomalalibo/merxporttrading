package com.merxport.trading.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.persistence.PrePersist;
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
public class PersistingBaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @BsonProperty("_id")
    @JsonProperty("_id")
    private String id;
    private Audit audit = new Audit();
    
    public PersistingBaseEntity()
    {
        super();
    }
    
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
    
    @PrePersist
    public void auditLog(boolean active)
    {
        if (!Objects.isNull(this.audit.getCreatedDate()))
        {
            this.audit.setCreatedBy("System");
            this.audit.setCreatedDate(LocalDateTime.now());
        }
        else
        {
            this.audit.setModifiedBy("System");
            this.audit.setModifiedDate(LocalDateTime.now());
        }
        if (!active)
        {
            this.audit.setArchivedBy("System");
            this.audit.setArchivedDate(LocalDateTime.now());
        }
    }
}
