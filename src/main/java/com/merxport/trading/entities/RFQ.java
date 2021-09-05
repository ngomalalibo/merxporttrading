package com.merxport.trading.entities;

import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.RFQPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rfqs")
public class RFQ extends PersistingBaseEntity
{
    private String title;
    private Commodity commodity;
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
    private String country;
    private CommercialTerms term;
    private String location;
    private String quality;
}
