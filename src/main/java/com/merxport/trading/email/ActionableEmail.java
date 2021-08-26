package com.merxport.trading.email;

import lombok.Data;

@Data
public class ActionableEmail
{
    private String message;
    private String toAddresses;
    private String fromAddresses;
    private String personName;
    private String subject;
    private String line1;
}
