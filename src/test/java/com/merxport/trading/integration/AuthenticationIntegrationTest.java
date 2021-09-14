package com.merxport.trading.integration;

import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.config.GenerateVerificationCode;
import com.merxport.trading.entities.Address;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.enumerations.UserType;
import com.merxport.trading.exception.DuplicateEntityException;
import com.merxport.trading.security.AuthenticationRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AuthenticationIntegrationTest extends AbstractIntegrationTest
{
    @Autowired
    private GenerateVerificationCode generateVerificationCode;
    
    @Test
    @DisplayName("should save file and then save user details")
    void addUser() throws Exception
    {
        File file = new File("./src/main/resources/static/images/team4.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), Files.probeContentType(file.toPath()), IOUtils.toByteArray(input));
        byte[] fileContent = multipartFile.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent)
        {
            @Override
            public String getFilename()
            {
                return file.getName();
            }
        };
        final HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(byteArrayResource, headers);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", requestEntity);
        
        final ParameterizedTypeReference<String> typeReference = new ParameterizedTypeReference<String>()
        {
        };
        
        ResponseEntity<String> imageID = restTemplate.postForEntity("/upload", body, String.class /*, typeReference*/);
        Address address = new Address("street", "city", "state", "country");
        
        User user = new User("Ngoo", "Alaliboo", "Martin", "ngomalalibo@yahoo.com", "password", "08974938292", Collections.singletonList(address), false, Scopes.DOMESTIC, imageID.getBody(), List.of(UserRole.BUYER), null, null, null, null, null, null, false, null, UserType.BUSINESS);
        
        ResponseEntity<User> userResponseEntity = restTemplate.postForEntity("https://merxporttrading.herokuapp.com/user", user, User.class);
        Assert.assertEquals(201, userResponseEntity.getStatusCode().value());
        Assert.assertEquals(MediaType.APPLICATION_JSON, userResponseEntity.getHeaders().getContentType());
        /*mockMvc.perform(MockMvcRequestBuilders.post("/user")
                                              .content(objectMapper.writeValueAsBytes(user)))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.status().isCreated());*/
        
        /** sending multipartfile and object test
         * MockMultipartFile secondFile = new MockMultipartFile("user", "", "application/json", json.getBytes());
         
         MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
         mockMvc.perform(MockMvcRequestBuilders.multipart("/user").file(multipartFile)
         .file(secondFile))
         .andExpect(MockMvcResultMatchers.status().isCreated());*/
    }
    
    @Test
    void authenticate() throws Exception
    {
        AuthenticationRequest authReq = new AuthenticationRequest("ngomalalibo@gmail.com", "passwordg");
        
        ResponseEntity<User> user = restTemplate.postForEntity("/auth", authReq, User.class);
        Assert.assertThrows("Duplicate Assert", DuplicateEntityException.class, () ->  {throw new DuplicateEntityException("Duplicate user. User exists!");});
        Assert.assertEquals(200, user.getStatusCode().value());
        Assert.assertEquals(MediaType.APPLICATION_JSON, user.getHeaders().getContentType());
        Assert.assertEquals("Ngo", Objects.requireNonNull(user.getBody()).getFirstName());
    }
    
    @Test
    void verifyUser() throws Exception
    {
        String code = "161196";
        String id = "613f30e50a472a0024e4393e";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
            put("code", code);
            
        }};
        
        User user = restTemplate.getForEntity("https://merxporttrading.herokuapp.com/user/verify/{id}/{code}", User.class, uriVars).getBody();
        assertNotNull(user);
        assertTrue(user.isVerified());
    }
    @Test
    void resendCode() throws Exception
    {
        String id = "61251d93a3c71d2e6115a414";
        Map<String, String> uriVars = new HashMap<>()
        {{
            put("id", id);
        
        }};
        User user = restTemplate.getForEntity("/user/{id}/resendCode", User.class, uriVars).getBody();
    }
}
