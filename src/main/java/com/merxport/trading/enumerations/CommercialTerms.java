package com.merxport.trading.enumerations;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum CommercialTerms
{
    EXW_WORKS("Exw Works"), FREE_CARRIER("Free Carrier"), FREE_ON_BOARD("Free On Board"),
    FREE_ALONG_SIDE("Free Along Side"), COST_AND_FREIGHT("Cost and Freight"), COST_INSURANCE_AND_FREIGHT("Cost Insurance and Freight"),
    CARRIAGE_PAID_TO("Carriage Paid To"), CARRIAGE_AND_INSURANCE_PAID_TO("Carriage and Insurance Paid To"), DELIVERED_AT_TERMINAL("Delivered at Terminal"),
    DELIVERED_AT_PLACE("Delivered at place"), DELIVERED_DUTY_PAID("Delivered Duty Paid");
    
    public static String getDisplayText(CommercialTerms i)
    {
        switch (i)
        {
            case EXW_WORKS:
                return "Exw Works";
            case FREE_CARRIER:
                return "Free Carrier";
            case FREE_ON_BOARD:
                return "Free On Board";
            case FREE_ALONG_SIDE:
                return "Free Along Side";
            case COST_AND_FREIGHT:
                return "Cost and Freight";
            case COST_INSURANCE_AND_FREIGHT:
                return "Cost Insurance and Freight";
            case CARRIAGE_PAID_TO:
                return "Carriage Paid To";
            case CARRIAGE_AND_INSURANCE_PAID_TO:
                return "Carriage and Insurance Paid To";
            case DELIVERED_AT_TERMINAL:
                return "Delivered at Terminal";
            case DELIVERED_AT_PLACE:
                return "Delivered at place";
            case DELIVERED_DUTY_PAID:
                return "Delivered Duty Paid";
            default:
                return "";
        }
    }
    
    private String value;
    
    CommercialTerms(String value)
    {
        this.value = value;
    }
    
    public static CommercialTerms fromValue(String v)
    {
        List<CommercialTerms> keys = new ArrayList<CommercialTerms>(Arrays.asList(CommercialTerms.values()));
        for (CommercialTerms c : CommercialTerms.values())
        {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
