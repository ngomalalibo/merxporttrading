package com.merxport.trading.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("")
public class UploadController
{
    @Autowired
    private UserService userService;
    
    @Qualifier("getObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException
    {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/upload").toUriString());
        return ResponseEntity.created(uri).body(userService.upload(file));
    }
    
    /*@PostMapping(value = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<User> addUser(@RequestParam("user") String user, @RequestParam("file") MultipartFile file) throws IOException
    {
        System.out.println("In controller >>>");
        User u = objectMapper.readValue(user, User.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(u);
        if (violations.size() > 0)
        {
            throw new ConstraintViolationException(violations);
        }
        
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        return ResponseEntity.created(uri).header("TOKEN", u.getToken()).body(userService.save(u, file));
    }*/
    
    
}
