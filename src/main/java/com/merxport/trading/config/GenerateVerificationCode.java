package com.merxport.trading.config;

import com.merxport.trading.security.VerificationPOJO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Random;

@Configuration
public class GenerateVerificationCode
{
    Random random = new Random();
    
    @Bean
    public VerificationPOJO verificationCode()
    {
        int number = random.nextInt(999999);
        String code = String.format("%06d", number);
        // this will convert any number sequence into 6 character.
        return new VerificationPOJO(code, LocalDateTime.now());
    }
}
