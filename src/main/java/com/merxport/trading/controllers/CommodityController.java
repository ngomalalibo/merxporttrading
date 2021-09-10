package com.merxport.trading.controllers;

import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.CommodityRequest;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.services.CommodityService;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class CommodityController
{
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private CommodityService commodityService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${pagination.page-size}")
    private int pageSize;
    
    @PostMapping("/commodity")
    public ResponseEntity<Commodity> addCommodity(@Valid @RequestBody Commodity commodity, @RequestParam("token") String token) throws IOException
    {
        commodity.setSessionUser(jwtTokenProvider.getUsername(token));
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/commodity").toUriString());
        return ResponseEntity.created(uri).body(commodityService.save(commodity));
    }
    
    @GetMapping("/commodity/{id}")
    public ResponseEntity<Commodity> getCommodity(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        Commodity commodity = commodityRepository.findById(id).orElseThrow(() ->
                                                                           {
                                                                               throw new EntityNotFoundException("Commodity not found");
                                                                           });
        return ResponseEntity.ok(commodity);
    }
    
    @PutMapping("/commodity")
    public ResponseEntity<Commodity> updateCommodity(@Valid @RequestBody Commodity commodity, @RequestParam("token") String token) throws IOException
    {
        commodity.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(commodityService.save(commodity));
    }
    
    @GetMapping("/commodities/{name}")
    public ResponseEntity<PageableResponse> getCommoditiesByName(@PathVariable(required = false) String name, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findByNameLikeOrderByNameAsc(name, page, pageSize));
        // Page<Commodity> byNameLikeOrderByNameAsc = commodityRepository.findByNameLikeOrderByNameAsc(name, Sort.by(Sort.Direction.ASC, "name"), PageRequest.of(page, pageSize));
        // return ResponseEntity.ok(byNameLikeOrderByNameAsc.getContent());
    }
    
    @GetMapping("/commodity/{id}/delete")
    public ResponseEntity<Commodity> deleteCommodity(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        Commodity commodity = commodityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commodity not found."));
        commodity.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(commodityService.delete(commodity));
    }
    
    @GetMapping("/{search}/commoditySearch")
    public ResponseEntity<PageableResponse> getCommoditiesSearch(@PathVariable String search, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(search, page, pageSize));
    }
    
    @GetMapping("/{category}/commodityByCategory")
    ResponseEntity<PageableResponse> findCommodityByCategoryLike(@PathVariable String category, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByCategoryLike(category, page, pageSize));
    }
    
    @GetMapping("/{country}/commodityByCountry")
    ResponseEntity<PageableResponse> findCommodityByCountry(@PathVariable String country, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByCountry(country, page, pageSize));
    }
    
    @GetMapping("/{amount}/commodityGreaterThan")
    ResponseEntity<PageableResponse> findCommodityByAmountGreaterThan(@PathVariable BigDecimal amount, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByAmountGreaterThan(amount, page, pageSize));
    }
    
    @GetMapping("/{amount}/commodityLessThan")
    ResponseEntity<PageableResponse> findCommodityByAmountLessThan(@PathVariable BigDecimal amount, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByAmountLessThan(amount, page, pageSize));
    }
    
    @GetMapping("/{scope}/commodityByScope")
    ResponseEntity<PageableResponse> findCommodityByScope(@PathVariable Scopes scope, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByScope(scope, page, pageSize));
    }
    
    @GetMapping("/{sellerID}/commodityBySeller")
    ResponseEntity<PageableResponse> findCommodityBySeller(@PathVariable String sellerID, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityBySeller(userService.findUser(sellerID), page, pageSize));
    }
    
    @PostMapping("/commodityMultiSearch")
    ResponseEntity<PageableResponse> findCommoditySearch(@RequestBody CommodityRequest request, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommoditySearch(request.getCountry(), request.getCategory(), request.getAmount(), Scopes.fromValue(request.getScope()), page, pageSize));
    }
}
