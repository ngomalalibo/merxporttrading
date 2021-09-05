package com.merxport.trading.services;


import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;

import java.math.BigDecimal;
import java.util.List;

public interface CommodityService
{
    Commodity save(Commodity c);
    
    Commodity delete(Commodity c);
    
    List<Commodity> findCommodityByCategoryLike(String category);
    
    List<Commodity> findCommodityByCountry(String country);
    
    List<Commodity> findCommodityByAmountGreaterThan(BigDecimal amount);
    
    List<Commodity> findCommodityByAmountLessThan(BigDecimal amount);
    
    List<Commodity> findCommodityByScope(Scopes scope);
    
    List<Commodity> findCommodityBySeller(User seller);
    
    List<Commodity> findCommoditySearch(String country, String category, BigDecimal amount, Scopes scopes);
}
