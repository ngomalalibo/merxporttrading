package com.merxport.trading.controllers;

import com.merxport.trading.entities.CommodityCategory;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController
{
    @Autowired
    private CategoryRepository categoryRepository;
    
    @PostMapping("/category")
    public ResponseEntity<CommodityCategory> addCategory(@RequestBody CommodityCategory commodityCategory, @RequestParam("token") String token)
    {
        return ResponseEntity.ok(categoryRepository.save(commodityCategory));
    }
    
    @GetMapping("/category/{id}")
    public ResponseEntity<CommodityCategory> getCategory(@PathVariable String id, @RequestParam("token") String token)
    {
        return ResponseEntity.ok(categoryRepository.findById(id).orElseThrow(() ->
                                                                             {
                                                                                 throw new EntityNotFoundException("Category not found");
                                                                             }));
    }
    
    @GetMapping("/categories/{name}")
    public ResponseEntity<List<CommodityCategory>> getCategories(@PathVariable(required = false) String name, @RequestParam("token") String token)
    {
        name = (name == null ? "" : name);
        return ResponseEntity.ok(categoryRepository.findByNameLikeOrderByNameAsc(name));
    }
    
    @DeleteMapping("/category/{id}")
    public void deleteCategory(@PathVariable String id)
    {
        categoryRepository.deleteById(id);
    }
    
    @PutMapping("/category")
    public ResponseEntity<CommodityCategory> updateCategory(@RequestBody CommodityCategory commodityCategory)
    {
        return ResponseEntity.ok(categoryRepository.save(commodityCategory));
    }
}
