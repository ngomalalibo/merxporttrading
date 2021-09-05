package com.merxport.trading.controllers;

import com.merxport.trading.entities.Quote;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.QuoteRepository;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.services.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuoteController
{
    @Autowired
    private QuoteService quoteService;
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private RFQRepository rfqRepository;
    
    @PostMapping("/quote")
    public ResponseEntity<Quote> addQuote(@RequestBody Quote quote, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.save(quote));
    }
    
    @GetMapping("/quote/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }
    
    @GetMapping("/quote/{id}/delete")
    public ResponseEntity<Quote> deleteQuote(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.delete(quoteRepository.findById(id).orElseThrow(EntityNotFoundException::new)));
    }
    
    @GetMapping("/quotes")
    public ResponseEntity<List<Quote>> getQuotes(@RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteRepository.findAll());
    }
    
    @GetMapping("/quotesActive")
    public ResponseEntity<List<Quote>> getQuotesActive(@RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.findAllActive());
    }
    
    @GetMapping("/quoteByRFQ/{id}")
    public ResponseEntity<List<Quote>> getQuoteByRFQ(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.findQuoteByRFQ(rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new)));
    }
    
    @GetMapping("/quote/{id}/accept")
    public ResponseEntity<Quote> acceptQuote(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.updateQuoteStatus(quoteRepository.findById(id).orElseThrow(EntityNotFoundException::new), QuoteStatus.ACCEPTED));
    }
    
    @GetMapping("/quotesAcceptedBySeller/{sellerID}")
    public ResponseEntity<List<Quote>> getAcceptedQuotesBySeller(@PathVariable String sellerID, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.findQuoteByStatusAndSeller(sellerID, QuoteStatus.ACCEPTED));
    }
    
    @GetMapping("/quotesAllBySeller/{sellerID}")
    public ResponseEntity<List<Quote>> getAllQuotesBySeller(@PathVariable String sellerID, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(quoteService.findAllQuotesBySeller(sellerID));
    }
}
