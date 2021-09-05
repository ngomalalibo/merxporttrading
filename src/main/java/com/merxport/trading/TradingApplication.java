package com.merxport.trading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@OpenAPIDefinition(info = @Info(title = "Merxport Trading API Documentation",
        version = "v0.0.1",
        description = "Better business and better service.",
        license = @io.swagger.v3.oas.annotations.info.License(name = "Merxport Commodities",
                url = "http://www.google.com")))
@Slf4j
@EnableMongoRepositories(basePackages = "com.merxport.trading.repositories")
// @EnableMongoAuditing
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TradingApplication extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder)
    {
        return builder.sources(TradingApplication.class);
    }
    
    public static void main(String[] args)
    {
        SpringApplication.run(TradingApplication.class, args);
    }
    
    
    @Bean
    public Gson getGsonBean()
    {
        return new GsonBuilder().setPrettyPrinting().create();
    }
    
    @Bean
    public ObjectMapper getObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
        
    }
    
}
