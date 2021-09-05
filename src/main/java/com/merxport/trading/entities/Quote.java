package com.merxport.trading.entities;

import com.merxport.trading.enumerations.QuoteStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "quotes")
public class Quote extends PersistingBaseEntity
{
    private RFQ rfq;
    private int quantity;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal rate;
    private Map<String, String> specification;
    private String QCDocumentation;
    private QuoteStatus quoteStatus;
    private LocalDate deliveryDate;
    private String remark;
    private String unit;
    private User seller;
}
