package com.merxport.trading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TradingApplication.class) // Loads Server
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureJsonTesters
// @WebAppConfiguration
public class AbstractIntegrationTest
{
    @Autowired
    protected WebApplicationContext webApplicationContext;
    
    @Qualifier("getObjectMapper")
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected RestTemplate restTemplate;
    
    protected MockMvc mockMvc;
    
    @Qualifier("getGsonBean")
    @Autowired
    protected Gson gson;
    
    @BeforeEach
    public void setup() throws Exception
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
