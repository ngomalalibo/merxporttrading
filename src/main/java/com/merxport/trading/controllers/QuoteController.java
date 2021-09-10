package com.merxport.trading.controllers;

import com.merxport.trading.entities.Quote;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.QuoteRepository;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.response.PageableResponse;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.services.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${pagination.page-size}")
    private int pageSize;
    
    @PostMapping("/quote")
    public ResponseEntity<Quote> addQuote(@Valid @RequestBody Quote quote, @RequestParam("token") String token) throws IOException
    {
        quote.setSessionUser(jwtTokenProvider.getUsername(token));
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
        Quote quote = quoteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        quote.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(quoteService.delete(quote));
    }
    
    @GetMapping("/quotes")
    public ResponseEntity<List<Quote>> getQuotes(@RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        Page<Quote> all = quoteRepository.findAll(PageRequest.of(page, pageSize));
        return ResponseEntity.ok(all.getContent());
    }
    
    @GetMapping("/quotesActive")
    public ResponseEntity<PageableResponse> getQuotesActive(@RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(quoteService.findAllActive(page, pageSize));
    }
    
    @GetMapping("/quoteByRFQ/{id}")
    public ResponseEntity<PageableResponse> getQuoteByRFQ(@PathVariable String id, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(quoteService.findQuoteByRFQ(rfqRepository.findById(id).orElseThrow(EntityNotFoundException::new), page, pageSize));
    }
    
    @GetMapping("/quote/{id}/accept")
    public ResponseEntity<Quote> acceptQuote(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        Quote quote = quoteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        quote.setSessionUser(jwtTokenProvider.getUsername(token));
        return ResponseEntity.ok(quoteService.updateQuoteStatus(quote, QuoteStatus.ACCEPTED));
    }
    
    @GetMapping("/quotesAcceptedBySeller/{sellerID}")
    public ResponseEntity<PageableResponse> getAcceptedQuotesBySeller(@PathVariable String sellerID, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(quoteService.findQuoteByStatusAndSeller(sellerID, QuoteStatus.ACCEPTED, page, pageSize));
    }
    
    @GetMapping("/quotesAllBySeller/{sellerID}")
    public ResponseEntity<PageableResponse> getAllQuotesBySeller(@PathVariable String sellerID, @RequestParam("token") String token, @RequestParam("page") int page) throws IOException
    {
        return ResponseEntity.ok(quoteService.findAllQuotesBySeller(sellerID, page, pageSize));
    }
}
