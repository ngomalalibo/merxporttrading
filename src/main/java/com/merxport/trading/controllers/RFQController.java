package com.merxport.trading.controllers;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.services.RFQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class RFQController
{
    @Autowired
    private RFQService rfqService;
    
    @Autowired
    private RFQRepository rfqRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${pagination.page-size}")
    private int pageSize;
    
    @PostMapping("/rfq")
    public ResponseEntity<RFQ> saveRFQ(@RequestBody RFQ rfq, @RequestParam("token") String token) throws IOException
    {
        rfq.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(rfqService.save(rfq));
    }
    
    @GetMapping("/rfq/{id}")
    public ResponseEntity<RFQ> getRFQ(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        RFQ rfq = rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        rfq.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(rfq);
    }
    
    @GetMapping("/rfq/{id}/delete")
    public ResponseEntity<RFQ> deleteRFQ(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        RFQ rfq = rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        rfq.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(rfqService.delete(rfq));
    }
    
    @GetMapping("/rfqByCommodityName/{name}")
    public ResponseEntity<PageableResponse> findByCommodityName(@PathVariable String name, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByCommodityNameLike(name, page, pageSize));
    }
    
    @GetMapping("/rfqByCountry/{country}")
    public ResponseEntity<PageableResponse> findByCountry(@PathVariable String country, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByCountry(country, page, pageSize));
    }
    
    @GetMapping("/rfqByTerm/{term}")
    public ResponseEntity<PageableResponse> findByTerm(@PathVariable String term, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByTerm(CommercialTerms.fromValue(term), page, pageSize));
    }
    
    @GetMapping("/rfqByTitle/{title}")
    public ResponseEntity<PageableResponse> findByTitle(@PathVariable String title, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(rfqService.findRFQByTitleLike(title, page, pageSize));
    }
}
