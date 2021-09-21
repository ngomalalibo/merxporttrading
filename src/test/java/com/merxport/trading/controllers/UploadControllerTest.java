package com.merxport.trading.controllers;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.services.UserService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UploadControllerTest extends AbstractIntegrationTest
{
    @Autowired
    private UserService userService;
    
    @Test
    void getImage()
    {
        String id = "6149ce8ad31ade7e30ed93d7";
        ResponseEntity<String> res = restTemplate.exchange("/getImage/{id}", HttpMethod.GET, jwtTokenProvider.getAuthorizationHeaderToken(), String.class, id);
        String image = res.getBody();
        System.out.println(image);
    }
    
    @Test
    void upload() throws Exception
    {
        File file = new File("./src/main/resources/static/images/dummy.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), Files.probeContentType(file.toPath()), IOUtils.toByteArray(input));
        
        String imageID = userService.upload(multipartFile);
        assertNotNull(imageID);
        String image = userService.getImage(imageID, 0, 0, null);
        System.out.println(image);
        assertNotNull(image);
        assertNotEquals("", image);
    }
}
