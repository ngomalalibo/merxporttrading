package com.merxport.trading.security;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerificationPOJO
{
    private String verificationCode;
    private LocalDateTime creationDateTime;
}
