package com.merxport.trading.controllers;

import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.entities.Commodity;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CommodityRepository;
import com.merxport.trading.services.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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
    
    @PostMapping("/commodity")
    public ResponseEntity<Commodity> addCommodity(@RequestBody Commodity commodity, @RequestParam("token") String token) throws IOException
    {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/commodity").toUriString());
        return ResponseEntity.created(uri).body(commodityRepository.save(commodity));
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
        return ResponseEntity.ok(commodityRepository.save(commodity));
    }
    
    @GetMapping("/commodities/{name}")
    public ResponseEntity<List<Commodity>> getCommoditiesByName(@PathVariable(required = false) String name, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityRepository.findByNameLikeOrderByNameAsc(name));
    }
    
    @GetMapping("/commodity/{id}/delete")
    public ResponseEntity<Commodity> deleteCommodity(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityService.deleteCommodity(id));
    }
    
    @GetMapping("/{search}/commodity")
    public ResponseEntity<List<Commodity>> getCommoditiesSearch(@PathVariable String search, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(commodityRepository.findByNameLikeOrDescriptionLikeOrCategoryLikeOrderByNameAsc(search));
    }
}
