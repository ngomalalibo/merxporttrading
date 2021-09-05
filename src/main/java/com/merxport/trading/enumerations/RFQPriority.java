package com.merxport.trading.enumerations;

import lombok.Getter;

@Getter
public enum RFQPriority
{
    LOW("Low"), MEDIUM("Medium"), HIGH("High");
    
    public static String getDisplayText(RFQPriority i)
    {
        switch (i)
        {
            case LOW:
                return "Low";
            case MEDIUM:
                return "Medium";
            case HIGH:
                return "High";
            default:
                return "";
        }
    }
    
    private String value;
    
    RFQPriority(String value)
    {
        this.value = value;
    }
    
    public static RFQPriority fromValue(String v)
    {
        for (RFQPriority c : RFQPriority.values())
        {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
