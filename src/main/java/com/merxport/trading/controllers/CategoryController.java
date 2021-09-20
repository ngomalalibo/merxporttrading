package com.merxport.trading.controllers;

import com.merxport.trading.entities.CommodityCategory;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.CategoryRepository;
import com.merxport.trading.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController
{
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /*@Value("${pagination.page-size}")
    private int pageSize;*/
    
    @PostMapping("/category")
    public ResponseEntity<CommodityCategory> addCategory(@Valid @RequestBody CommodityCategory commodityCategory, HttpServletRequest req) throws IOException, ServletException
    {
        commodityCategory.setSessionUser(jwtTokenProvider.getUsername(jwtTokenProvider.getTokenFromRequestHeader(req)));
        return ResponseEntity.ok(categoryRepository.save(commodityCategory));
    }
    
    @GetMapping("/category/{id}")
    public ResponseEntity<CommodityCategory> getCategory(@PathVariable String id)
    {
        return ResponseEntity.ok(categoryRepository.findById(id).orElseThrow(() ->
                                                                             {
                                                                                 throw new EntityNotFoundException("Category not found");
                                                                             }));
    }
    
    @GetMapping("/categories/{name}")
    public ResponseEntity<List<CommodityCategory>> getCategories(@PathVariable(required = false) String name, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize)
    {
        name = (name == null ? "" : name);
        return ResponseEntity.ok(categoryRepository.findByNameLikeOrderByNameAsc(name, PageRequest.of(page, pageSize)));
    }
    
    @DeleteMapping("/category/{id}")
    public void deleteCategory(@PathVariable String id)
    {
        categoryRepository.deleteById(id);
    }
    
    @PutMapping("/category")
    public ResponseEntity<CommodityCategory> updateCategory(@RequestBody CommodityCategory commodityCategory, HttpServletRequest req) throws IOException, ServletException
    {
        commodityCategory.setSessionUser(jwtTokenProvider.getUsername(jwtTokenProvider.getTokenFromRequestHeader(req)));
        return ResponseEntity.ok(categoryRepository.save(commodityCategory));
    }
}
