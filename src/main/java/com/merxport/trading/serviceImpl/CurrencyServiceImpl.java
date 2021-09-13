package com.merxport.trading.serviceImpl;

import com.merxport.trading.services.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class CurrencyServiceImpl implements CurrencyService
{
    @Override
    public Currency getCurrency(String code)
    {
        try
        {
            return Currency.getInstance(code);
        }
        catch (IllegalArgumentException exception)
        {
            throw new IllegalArgumentException("Currency code not found");
        }
    }
    
    @Override
    public Map<String, Currency> getCurrencies()
    {
        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
        Map<String, Currency> currMap = new HashMap<>();
        availableCurrencies.forEach(curr ->
                                    {
                                        currMap.put(curr.getCurrencyCode() + " " + curr.getDisplayName(), curr);
                                    });
        return currMap;
    }
    
    public static void main(String[] args)
    {
        Map<String, Currency> currencies = new CurrencyServiceImpl().getCurrencies();
        // currencies.keySet().forEach(System.out::println);
        System.out.println(new CurrencyServiceImpl().getCurrency("NGN").getDisplayName());
    }
}
