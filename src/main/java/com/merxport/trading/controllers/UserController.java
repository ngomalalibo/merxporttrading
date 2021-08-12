package com.merxport.trading.controllers;

import com.merxport.trading.entities.User;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController
{
    @Autowired
    private UserService userService;
    
    public static final String API_KEY = System.getenv().get("PACKAGETRACKER_KEY");
    
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody User user, @RequestParam("file") MultipartFile file, @RequestParam("key") String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.save(user, file));
    }
    
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id, @RequestParam("key") String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.findUser(id));
        
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUser(@RequestParam("key") String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.getActiveUsers());
        
    }
    
    // @DeleteMapping("/user/{id}")
    @PutMapping("/user/delete/{id}")// archive user data
    public ResponseEntity<User> deleteUser(@PathVariable String id, @RequestParam(value = "key", required = false) String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.deleteUser(id));
    }
    
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestParam("file") MultipartFile file, @RequestParam("key") String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.save(user, file));
    }
    
    @PutMapping("/user/verify/{id}")
    public ResponseEntity<User> verifyUser(@PathVariable String id, @RequestParam(value = "key", required = false) String key) throws IOException
    {
        if (!key.equals(API_KEY))
        {
            throw new AccessDeniedException("Access denied. Provide a valid Key.");
        }
        return ResponseEntity.ok(userService.verifyAccount(id));
    }
}
