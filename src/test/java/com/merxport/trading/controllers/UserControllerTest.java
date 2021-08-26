package com.merxport.trading.controllers;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest extends AbstractIntegrationTest
{
    
    @Test
    void getUser()
    {
        String id = "6126806273aade16270429c4";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        User body = restTemplate.getForEntity("/api/user/{id}?token=" + AuthenticationController.TOKEN, User.class, uriVars).getBody();
        assertNotNull(body);
        assertEquals("Ngo", body.getFirstName());
        
    }
    
    @Test
    void getUsers()
    {
        // two approaches to returning lists using resttemplate
        Map<String, String> uriVars = new HashMap<>()
        {{
        }};
        User[] body = restTemplate.getForEntity("/api/users?token=" + AuthenticationController.TOKEN, User[].class, uriVars).getBody();
        assertNotNull(body);
        assertEquals("Ngo", body[0].getFirstName());
        
        ResponseEntity<List<User>> exchange = restTemplate.exchange("/api/users?token=" + AuthenticationController.TOKEN, HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>()
        {
        });
        List<User> users = exchange.getBody();
        assertNotNull(users);
        assertEquals("Ngo", users.get(0).getFirstName());
        
    }
    
    @Test
    void deleteUser()
    {
        String id = "6126806273aade16270429c4";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        }};
        User body = restTemplate.getForEntity("/api/user/delete/{id}?token=" + AuthenticationController.TOKEN, User.class, uriVars).getBody();
        assertNotNull(body);
        assertFalse(body.isActive());
    }
}
