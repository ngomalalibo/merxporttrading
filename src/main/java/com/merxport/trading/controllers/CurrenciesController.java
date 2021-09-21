package com.merxport.trading.controllers;

import com.merxport.trading.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrenciesController
{
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/currency/{code}")
    public Currency getCurrency(@PathVariable String code)
    {
        return currencyService.getCurrency(code);
    }
    
    @GetMapping("/currencies")
    public List<String> getCurrencies()
    {
        return new ArrayList<>(currencyService.getCurrencies().keySet());
    }
    
}
