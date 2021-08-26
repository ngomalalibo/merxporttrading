package com.merxport.trading.controllers;

import com.merxport.trading.entities.User;
import com.merxport.trading.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController
{
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(userService.findUser(id));
        
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(userService.getActiveUsers());
    }
    
    @GetMapping("/user/delete/{id}")// archive user data
    public ResponseEntity<User> deleteUser(@PathVariable String id, @RequestParam("token") String token) throws IOException
    {
        return ResponseEntity.ok(userService.deleteUser(userService.findUser(id)));
    }
    
    /*@PutMapping("/user/{email}/addRole")
    public ResponseEntity<User> addRoleToUser(@PathVariable String email,
                                              @RequestParam(value = "key", required = false) String key,
                                              @RequestParam String token,
                                              @RequestParam String role)
    {
        return ResponseEntity.ok().body(userService.addRoleToUser(email, UserRole.fromValue(role)));
    }*/
    
    
}
