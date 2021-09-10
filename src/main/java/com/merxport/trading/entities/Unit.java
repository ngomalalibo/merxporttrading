package com.merxport.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "units")
public class Unit extends PersistingBaseEntity
{
    @NotBlank(message = "Name is mandatory")
    private String singularName;
    private String pluralName;
}
