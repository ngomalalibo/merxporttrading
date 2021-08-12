package com.merxport.trading;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@OpenAPIDefinition(info = @Info(title = "Merxport Commodities API Documentation",
        version = "v0.0.1",
        description = "Better business and better service.",
        license = @io.swagger.v3.oas.annotations.info.License(name = "Merxport Commodities",
                url = "http://www.google.com")))
@Slf4j
@EnableMongoRepositories(basePackages = "com.merxport.trading.repositories")
@SpringBootApplication
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
    
}
