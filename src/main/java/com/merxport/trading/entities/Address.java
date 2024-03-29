package com.merxport.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable
{
    private String street;
    private String city;
    private String state;
    private String country;
}
