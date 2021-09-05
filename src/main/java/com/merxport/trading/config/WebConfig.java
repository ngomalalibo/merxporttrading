package com.merxport.trading.config;

import com.merxport.trading.email.SendMail_Working;
import com.merxport.trading.serviceImpl.DeleteServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({"com.merxport.trading.controllers"})
@EnableWebMvc
public class WebConfig extends WebMvcConfigurationSupport
{
    @Bean
    public MultipartResolver multipartResolver()
    {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
        // return new CommonsMultipartResolver();
    }
    
    @Bean
    public SendMail_Working sendMail()
    {
        return new SendMail_Working();
    }
    
    @Bean
    public DeleteServiceImpl deleteService()
    {
        return new DeleteServiceImpl();
    }
}
