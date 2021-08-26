package com.merxport.trading.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.TradingApplication;
import com.merxport.trading.repositories.UserRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TradingApplication.class) // Loads Server
@AutoConfigureMockMvc // does not start server. Loads context
public abstract class AbstractControllerTest
{
    // place mockbeans and other
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Before
    public void setup()
    {
    
    }
}
