package com.merxport.trading.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;

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
}
