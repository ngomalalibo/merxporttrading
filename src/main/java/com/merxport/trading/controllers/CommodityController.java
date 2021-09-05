package com.merxport.trading.controllers;

import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.CommodityRequest;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.services.CommodityService;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

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
    
    @PostMapping("/commodity")
    public ResponseEntity<Commodity> addCommodity(@RequestBody Commodity commodity, @RequestParam("token") String token) throws IOException
    {
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
    public ResponseEntity<Commodity> updateCommodity(@RequestBody Commodity commodity, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.save(commodity));
    }
    
    @GetMapping("/commodities/{name}")
    public ResponseEntity<List<Commodity>> getCommoditiesByName(@PathVariable(required = false) String name, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityRepository.findByNameLikeOrderByNameAsc(name));
    }
    
    @GetMapping("/commodity/{id}/delete")
    public ResponseEntity<Commodity> deleteCommodity(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.delete(commodityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commodity not found."))));
    }
    
    @GetMapping("/{search}/commoditySearch")
    public ResponseEntity<List<Commodity>> getCommoditiesSearch(@PathVariable String search, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityRepository.findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(search));
    }
    
    @GetMapping("/{category}/commodityByCategory")
    ResponseEntity<List<Commodity>> findCommodityByCategoryLike(@PathVariable String category, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByCategoryLike(category));
    }
    
    @GetMapping("/{country}/commodityByCountry")
    ResponseEntity<List<Commodity>> findCommodityByCountry(@PathVariable String country, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByCountry(country));
    }
    
    @GetMapping("/{amount}/commodityGreaterThan")
    ResponseEntity<List<Commodity>> findCommodityByAmountGreaterThan(@PathVariable BigDecimal amount, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByAmountGreaterThan(amount));
    }
    
    @GetMapping("/{amount}/commodityLessThan")
    ResponseEntity<List<Commodity>> findCommodityByAmountLessThan(@PathVariable BigDecimal amount, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByAmountLessThan(amount));
    }
    
    @GetMapping("/{scope}/commodityByScope")
    ResponseEntity<List<Commodity>> findCommodityByScope(@PathVariable Scopes scope, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityByScope(scope));
    }
    
    @GetMapping("/{sellerID}/commodityBySeller")
    ResponseEntity<List<Commodity>> findCommodityBySeller(@PathVariable String sellerID, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommodityBySeller(userService.findUser(sellerID)));
    }
    
    @PostMapping("/commodityMultiSearch")
    ResponseEntity<List<Commodity>> findCommoditySearch(@RequestBody CommodityRequest request, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.findCommoditySearch(request.getCountry(), request.getCategory(), request.getAmount(), Scopes.fromValue(request.getScope())));
    }
}
