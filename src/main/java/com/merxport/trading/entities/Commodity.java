package com.merxport.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private BigDecimal rate;
    private int quantity;
    private String unit;
    private User seller;
}
