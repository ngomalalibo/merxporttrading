package com.merxport.trading.config;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.UserScopes;
import com.merxport.trading.security.VerificationPOJO;
import com.merxport.trading.serviceImpl.UserServiceImpl;
import com.merxport.trading.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

// import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@TestConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class MyTestConfigurations
{
    /*@Bean
    public WebClient getWebClient(final WebClient.Builder builder, @Value("${data.service.endpoint}") String url)
    {
        WebClient webClient = builder.baseUrl(url)
                                     .defaultHeader(
                                             HttpHeaders.CONTENT_TYPE,
                                             MediaType.APPLICATION_JSON_VALUE)
                                     .build();
        log.info("WebClient Bean Instance: {}", webClient);
        return webClient;
    }*/
    
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
                return User.builder().firstName("fffddddddddddfname").lastName("testlastName").email("s@email.com").password("pass").build();
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
}
