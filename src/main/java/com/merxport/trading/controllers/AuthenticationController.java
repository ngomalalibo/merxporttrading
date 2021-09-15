package com.merxport.trading.controllers;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.entities.User;
import com.merxport.trading.security.AuthenticationRequest;
import com.merxport.trading.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@Slf4j
@RestController
public class AuthenticationController
{
    @Autowired
    private UserService userService;
    
    public final static String TOKEN = System.getenv().get("MERXPORT_TOKEN");
    
    
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) throws IOException, UnirestException
    {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        // return ResponseEntity.created(uri).header("TOKEN", user.getToken()).body(userService.save(user));
        return ResponseEntity.created(uri).body(userService.save(user));
    }
    
    @PutMapping("/user")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws IOException, UnirestException
    {
        return ResponseEntity.ok(userService.save(user));
    }
    
    @PostMapping("/auth")
    public ResponseEntity<User> login(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request) throws Exception
    {
        User login = userService.authenticateUser(authenticationRequest.getUsername(), authenticationRequest.getPassword(), request);
        return ResponseEntity.ok(login);
    }
    
    @GetMapping("/user/verify/{id}/{code}")
    public ResponseEntity<User> verifyUser(@PathVariable String id, @PathVariable String code) throws IOException
    {
        return ResponseEntity.ok(userService.verifyAccount(id, code));
    }
    
    @GetMapping("/user/{id}/resendCode")
    public ResponseEntity<?> resendCode(@PathVariable String id) throws UnirestException
    {
        userService.resendCode(id);
        return ResponseEntity.ok(null);
    }
    
    @GetMapping("/user/{id}/resetPassword/{username}")
    public ResponseEntity<?> resetPassword(@PathVariable String id, @PathVariable String username) throws UnirestException
    {
        /*userService.resendCode(id);
        return ResponseEntity.ok(null);*/
        return null;
    }
    
    @GetMapping("/test/{name}")
    public ResponseEntity<String> dummy(@PathVariable String name)
    {
        return ResponseEntity.ok().body("Welcome " + name + "!!!");
    }
    
    
    
    /*@GetMapping("/login")
    public ResponseEntity<User> login(@RequestBody AuthenticationRequest authenticationRequest,
                                      @RequestParam(value = "key", required = false) String key,
                                      @RequestParam String token, HttpServletRequest request) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        User login = userService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword(), token, request.getSession());
        if (login == null)
        {
            throw new AccessDeniedException("Login was not successful");
        }
        return ResponseEntity.ok(login);
    }*/
}
