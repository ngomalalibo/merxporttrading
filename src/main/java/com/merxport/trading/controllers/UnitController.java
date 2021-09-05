package com.merxport.trading.controllers;

import com.merxport.trading.entities.Unit;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UnitController
{
    @Autowired
    private UnitRepository unitRepository;
    
    @PostMapping("/unit")
    public ResponseEntity<Unit> addUnit(@RequestBody Unit unit, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(unitRepository.save(unit));
    }
    
    @GetMapping("/unit/{id}")
    public ResponseEntity<Unit> getUnit(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(unitRepository.findById(id).orElseThrow(() ->
                                                                         {
                                                                             throw new EntityNotFoundException("Unit not found");
                                                                         }));
    }
    
    @GetMapping("/units/{name}")
    public ResponseEntity<List<Unit>> getUnits(@PathVariable(required = false) String name, @RequestParam("token") String token) throws IOException
    {
        name = (name == null ? "" : name);
        return ResponseEntity.ok(unitRepository.findBySingularNameLikeOrderBySingularNameAsc(name));
    }
    
    @DeleteMapping("/unit/{id}")
    public void deleteUnit(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        unitRepository.deleteById(id);
    }
    
    @PutMapping("/unit")
    public ResponseEntity<Unit> updateUnit(@RequestBody Unit unit, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(unitRepository.save(unit));
    }
}
