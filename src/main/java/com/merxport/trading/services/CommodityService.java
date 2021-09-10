package com.merxport.trading.services;


import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.response.PageableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CommodityService
{
    Commodity save(Commodity c);
    
    Commodity delete(Commodity c);
    
    PageableResponse findCommodityByCategoryLike(String category, int page, int pageSize);
    
    PageableResponse findCommodityByCountry(String country, int page, int pageSize);
    
    PageableResponse findCommodityByAmountGreaterThan(BigDecimal amount, int page, int pageSize);
    
    PageableResponse findCommodityByAmountLessThan(BigDecimal amount, int page, int pageSize);
    
    PageableResponse findCommodityByScope(Scopes scope, int page, int pageSize);
    
    PageableResponse findCommodityBySeller(User seller, int page, int pageSize);
    
    PageableResponse findCommoditySearch(String country, String category, BigDecimal amount, Scopes scopes, int page, int pageSize);
    
    PageableResponse findByNameLikeOrderByNameAsc(String name, int page, int pageSize);
    PageableResponse findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(String name, int page, int pageSize);
}
