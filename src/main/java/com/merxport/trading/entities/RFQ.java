package com.merxport.trading.entities;

import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "rfqs")
public class RFQ extends PersistingBaseEntity
{
    @NotBlank(message = "Title is mandatory")
    private String title;
    private String commodityID;
    private BigDecimal rate;
    private String unit;
    private int quantityRequired;
    private Map<String, String> specification;
    private String QCDocumentation;
    private RFQPriority priority;
    private int minQuantity;
    private int maxQuantity;
    private LocalDateTime dateNeeded;
    private Currency currency;
    private String sampleImageID;
    @Transient
    @BsonIgnore
    private String sampleImage;
    private String country;
    private CommercialTerms term;
    private String location;
    private String quality;
    private String buyerID;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal minPrice;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal maxPrice;
}
