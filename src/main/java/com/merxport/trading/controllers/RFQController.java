package com.merxport.trading.controllers;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.services.RFQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RFQController
{
    @Autowired
    private RFQService rfqService;
    
    @Autowired
    private RFQRepository rfqRepository;
    
    @PostMapping("/rfq")
    public ResponseEntity<RFQ> saveRFQ(@RequestBody RFQ rfq, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.save(rfq));
    }
    
    @GetMapping("/rfq/{id}/delete")
    public ResponseEntity<RFQ> deleteRFQ(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.delete(rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new)));
    }
    
    @GetMapping("/rfqByCommodityName/{name}")
    public ResponseEntity<List<RFQ>> findByCommodityName(@PathVariable String name, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByCommodityNameLike(name));
    }
    
    @GetMapping("/rfqByCountry/{country}")
    public ResponseEntity<List<RFQ>> findByCountry(@PathVariable String country, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByCountry(country));
    }
    
    @GetMapping("/rfqByTerm/{term}")
    public ResponseEntity<List<RFQ>> findByTerm(@PathVariable String term, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByTerm(CommercialTerms.fromValue(term)));
    }
    
    @GetMapping("/rfqByTitle/{title}")
    public ResponseEntity<List<RFQ>> findByTitle(@PathVariable String title, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByTitleLike(title));
    }
}
