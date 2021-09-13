package com.merxport.trading.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyModel
{
    private String countryCode;
    private int defaultFractionDigits;
    private int numericCode;
    private String displayName;
    private String numericCodeAsString;
    private String symbol;
}
