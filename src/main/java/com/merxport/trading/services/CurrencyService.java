package com.merxport.trading.services;

import java.util.Currency;
import java.util.Map;

public interface CurrencyService
{
    public Currency getCurrency(String code);
    
    public Map<String, Currency> getCurrencies();
}
