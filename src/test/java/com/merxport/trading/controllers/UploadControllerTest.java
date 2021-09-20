package com.merxport.trading.controllers;

import com.merxport.trading.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UploadControllerTest extends AbstractIntegrationTest
{
    
    @Test
    void getImage()
    {
        String id = "6126a4817f80646d7836a04f";
        ResponseEntity<String> res = restTemplate.getForEntity("/getImage/{id}", String.class, id);
        String image = res.getBody();
        System.out.println(image);
    }
}
