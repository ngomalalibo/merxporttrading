package com.merxport.trading.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationPOJO
{
    private String verificationCode;
    private LocalDateTime creationDateTime;
}
