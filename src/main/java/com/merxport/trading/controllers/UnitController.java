package com.merxport.trading.controllers;

import com.merxport.trading.entities.Unit;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.UnitRepository;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.services.UnitService;
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
public class UnitController
{
    @Autowired
    private UnitService unitService;
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/unit")
    public ResponseEntity<Unit> addUnit(@Valid @RequestBody Unit unit, HttpServletRequest req) throws IOException, ServletException
    {
        unit.setSessionUser(jwtTokenProvider.getUsername(jwtTokenProvider.getTokenFromRequestHeader(req)));
        return ResponseEntity.ok(unitService.save(unit));
    }
    
    @GetMapping("/unit/{id}")
    public ResponseEntity<Unit> getUnit(@PathVariable String id) throws IOException
    {
        return ResponseEntity.ok(unitRepository.findById(id).orElseThrow(() ->
                                                                         {
                                                                             throw new EntityNotFoundException("Unit not found");
                                                                         }));
    }
    
    @GetMapping("/units/{name}")
    public ResponseEntity<List<Unit>> getUnitsByName(@PathVariable(required = false) String name) throws IOException
    {
        name = (name == null ? "" : name);
        return ResponseEntity.ok(unitRepository.findBySingularNameLikeOrderBySingularNameAsc(name, PageRequest.of(0, 6)));
    }
    
    @GetMapping("/units")
    public ResponseEntity<List<Unit>> getAllUnits() throws IOException
    {
        return ResponseEntity.ok(unitRepository.findAll());
    }
    
    @DeleteMapping("/unit/{id}")
    public void deleteUnit(@PathVariable String id) throws IOException
    {
        unitRepository.deleteById(id);
    }
    
    @PutMapping("/unit")
    public ResponseEntity<Unit> updateUnit(@Valid @RequestBody Unit unit) throws IOException
    {
        return ResponseEntity.ok(unitRepository.save(unit));
    }
}
