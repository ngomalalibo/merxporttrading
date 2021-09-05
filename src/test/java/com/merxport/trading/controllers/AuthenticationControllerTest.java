package com.merxport.trading.controllers;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.exception.DuplicateEntityException;
import com.merxport.trading.security.AuthenticationRequest;
import com.merxport.trading.security.VerificationPOJO;
import com.merxport.trading.services.UserService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends AbstractControllerTest
{
    @Autowired
    private UserService userService;
    
    @Autowired
    AuthenticationController authenticationController;
    
    @Test
    public void contextLoads() throws Exception
    {
        Assertions.assertThat(authenticationController).isNotNull();
    }
    
    @org.junit.Test(expected = DuplicateEntityException.class)
    @Test
    void addUser() throws Exception
    {
        User user = User.builder().firstName("Ngo").lastName("Alalibo").email("ngomalalibo@gmail.com").password("password")
                                 .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).userRoles(List.of(UserRole.BUYER, UserRole.SELLER)).build();
        User save = userService.save(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/user").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(save)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName").value("TestFName"))
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateEntityException))
               .andExpect(jsonPath("$.lastName").value("testlastName"));
    }
    
    @Test
    void updateUser() throws Exception
    {
        User save = userService.save(new User());
        mockMvc.perform(MockMvcRequestBuilders.put("/user").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(save)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("TestFName"))
               .andExpect(jsonPath("$.lastName").value("testlastName"));
    }
    
    @Test
    void login() throws Exception
    {
        // fname pass
        AuthenticationRequest rew = new AuthenticationRequest("ugochukwu@qa.team", "Ugochukwu_1");
        mockMvc.perform(MockMvcRequestBuilders.post("/auth").contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(rew)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Ugochukwu"))
               .andExpect(jsonPath("$.password").value("$2a$11$uXztwExxbtQ3.rteb2qxZuuu8LQ2t9FB3U19OnmAUf0z8CZCUpYxa"));
    }
    
    @Test
    void verifyUser() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.put("/user/verify/{id}/{code}", "1", "123456").contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("fname"))
               .andExpect(jsonPath("$.verified").value(true))
               .andExpect(jsonPath("$.verificationPOJO.verificationCode").value("123456"))
               .andExpect(jsonPath("$.password").value("pass"));
        
    }
    
    @Test
    void addRoleToUser() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{email}/addRole", "e@mail.com").contentType(MediaType.APPLICATION_JSON_VALUE)
                                              .param("role", UserRole.BUYER.getValue())
                                              .param("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInBhc3N3b3JkIjoiJDJhJDExJFMvSFdNMk9SUTBPalNKUmZUbU9Gd2VjZUxPbzJRQ1lGSDYwanl1dlBNb28uVnFjUmo2NDhtIiwicm9sZXMiOlsiQlVZRVIiXSwiaWF0IjoxNjI5NzY0MzEyfQ._eu4cdGir-8Ud2QL48ZdVSdvhY8XFsvPRiuOGCkuJis"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("fname"))
               .andExpect(jsonPath("$.verified").value(true))
               .andExpect(jsonPath("$.verificationPOJO.verificationCode").value("123456"))
               .andExpect(jsonPath("$.password").value("pass"))
               .andExpect(jsonPath("$.userRoles", Matchers.contains("BUYER", "SELLER")))
               .andExpect(jsonPath("$.userRoles").isArray())
               .andExpect(jsonPath("$.userRoles", hasSize(2)));
    }
    
    /*@TestConfiguration
    static class UserServiceImplTestContextConfiguration
    {
        @Bean
        public UserService userService()
        {
            return new UserServiceImpl()
            {
                public User save(User user)
                {
                    return User.builder().firstName("TestFName").lastName("testlastName").email("s@email.com").password("passowrd").build();
                }
                
                public User authenticate(String username, String password)
                {
                    return User.builder().firstName("ffffname").lastName("testlastName").email("s@email.com").password("pass").build();
                }
                
                public User addRoleToUser(String email, UserRole role)
                {
                    return User.builder().firstName("fname").lastName("testlastName").email("s@email.com").password("pass")
                               .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).userRoles(List.of(UserRole.BUYER, UserRole.SELLER)).build();
                }
                
                public String upload(MultipartFile file) throws IOException
                {
                    return null;
                }
                
                public List<User> getActiveUsers() throws IOException
                {
                    return null;
                }
                
                public List<User> getArchivedUsers() throws IOException
                {
                    return null;
                }
                
                public User deleteUser(User user)
                {
                    return new User();
                }
                
                public void sendMessage()
                {
                
                }
                
                public User verifyAccount(String id, String code)
                {
                    return User.builder().firstName("fname").lastName("testlastName").email("s@email.com").password("pass")
                               .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).build();
                }
                
                public void setScope(UserScopes scope)
                {
                
                }
            };
        }
    }*/
}
