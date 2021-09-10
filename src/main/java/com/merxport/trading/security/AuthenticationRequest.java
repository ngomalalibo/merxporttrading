package com.merxport.trading.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationRequest
{
    @Email(message = "Email is mandatory")
    @NotNull
    private String username;
    @NotNull(message = "Password is mandatory")
    private String password;
}
