package com.merxport.trading.entities;

import com.merxport.trading.enumerations.Scopes;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Document(collection = "commodities")
public class Commodity extends PersistingBaseEntity
{
    private String name;
    private List<String> category;
    @Column(length = 500)
    private String description;
    private Map<String, String> specification;
    @Column(length = 500)
    private String QCDocumentation;
    private List<String> photoIds = new ArrayList<>();
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal rate;
    private int quantity;
    private String unit;
    @BsonProperty(useDiscriminator = true)
    private User seller;
    private String country;
    private Scopes scope;
}
