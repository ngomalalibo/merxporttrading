package com.merxport.trading.controllers;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Quote;
import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.QuoteStatus;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.QuoteRepository;
import com.merxport.trading.repositories.RFQRepository;
import com.merxport.trading.repositories.UserRepository;
import com.merxport.trading.services.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class QuoteControllerTest extends AbstractIntegrationTest
{
    private FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());
    private final Faker faker = new Faker(Locale.getDefault());
    
    @Autowired
    private QuoteService quoteService;
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private RFQRepository rfqRepository;
    
    private Quote quote;
    
    private final ParameterizedTypeReference<List<Quote>> typeReference = new ParameterizedTypeReference<List<Quote>>()
    {
    };
    
    RFQ rfq;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    public void setup()
    {
        rfq = rfqRepository.findById("6132233e12e8966bf50d04eb").orElseThrow(EntityNotFoundException::new);
        // User seller = userRepository.findById("6126806273aade16270429c4").orElseThrow(EntityNotFoundException::new);
        // quote = quoteRepository.findById("6134de7d4da8cf2229f352d3").orElseThrow(EntityNotFoundException::new);
        // quote.setSeller(seller);
        // quote = new Quote(rfq, faker.number().numberBetween(5, 100), new BigDecimal(40000), null, "QCDoc", QuoteStatus.PENDING, LocalDate.now(), "remark2", "Bag");
        quote = quoteRepository.findById("6134de7d4da8cf2229f352d3").orElseThrow(EntityNotFoundException::new);
    }
    
    @Test
    void addQuote()
    {
        ResponseEntity<Quote> quoteResponseEntity = restTemplate.postForEntity("/api/quote?token=" + AuthenticationController.TOKEN, quote, Quote.class);
        Quote q = quoteResponseEntity.getBody();
        assertNotNull(q);
        assertNotNull(q.getId());
        assertEquals(q.getRemark(), "remark");
    }
    
    @Test
    void getQuote()
    {
        ResponseEntity<Quote> getQuote = restTemplate.getForEntity("/api/quote/{id}?token=" + AuthenticationController.TOKEN, Quote.class, "6134de7d4da8cf2229f352d3");
        assertNotNull(getQuote.getBody());
        assertEquals(getQuote.getBody().getId(), "6134de7d4da8cf2229f352d3");
    }
    
    @Test
    void deleteQuote()
    {
        ResponseEntity<Quote> getQuote = restTemplate.getForEntity("/api/quote/{id}/delete?token=" + AuthenticationController.TOKEN, Quote.class, "6134de7d4da8cf2229f352d3");
        assertNotNull(getQuote.getBody());
        assertFalse(getQuote.getBody().isActive());
    }
    
    @Test
    void getQuotes()
    {
        ResponseEntity<List<Quote>> getQuotes = restTemplate.exchange("/api/quotes?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference);
        assertNotNull(getQuotes.getBody());
        assertEquals(2, getQuotes.getBody().size());
    }
    
    @Test
    void getQuotesActive()
    {
        ResponseEntity<List<Quote>> getQuotes = restTemplate.exchange("/api/quotesActive?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference);
        assertNotNull(getQuotes.getBody());
        assertEquals(1, getQuotes.getBody().size());
    }
    
    @Test
    void getQuoteByRFQ()
    {
        ResponseEntity<List<Quote>> getQuotes = restTemplate.exchange("/api/quoteByRFQ/{id}?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, "6132233e12e8966bf50d04eb");
        assertNotNull(getQuotes.getBody());
        assertEquals(1, getQuotes.getBody().size());
        assertEquals(getQuotes.getBody().get(0).getRfq().getTitle(), "Gorgeous Granite Gloves");
    }
    
    @Test
    void acceptQuote()
    {
        ResponseEntity<Quote> getQuotes = restTemplate.exchange("/api/quote/{id}/accept?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, Quote.class, "6134e2dc0be17e28608afaa8");
        assertNotNull(getQuotes.getBody());
        assertEquals(getQuotes.getBody().getQuoteStatus(), QuoteStatus.PENDING);
    }
    
    @Test
    void getAcceptedQuotesBySeller()
    {
        ResponseEntity<List<Quote>> getQuotes = restTemplate.exchange("/api/quotesAcceptedBySeller/{sellerID}?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, "6126806273aade16270429c4");
        assertNotNull(getQuotes.getBody());
        assertEquals(1, getQuotes.getBody().size());
        assertEquals(getQuotes.getBody().get(0).getRfq().getTitle(), "Gorgeous Granite Gloves");
    }
    
    @Test
    void getAllQuotesBySeller()
    {
        ResponseEntity<List<Quote>> getQuotes = restTemplate.exchange("/api/quotesAllBySeller/{sellerID}?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, typeReference, "6126806273aade16270429c4");
        assertNotNull(getQuotes.getBody());
        assertEquals(1, getQuotes.getBody().size());
        assertEquals(getQuotes.getBody().get(0).getRfq().getTitle(), "Gorgeous Granite Gloves");
    }
}
