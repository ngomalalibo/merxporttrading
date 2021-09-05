package com.merxport.trading.enumerations;

import lombok.Getter;

@Getter
public enum Scopes
{
    DOMESTIC("Domestic"), INTERNATIONAL("International");
    
    public static String getDisplayText(Scopes i)
    {
        switch (i)
        {
            case DOMESTIC:
                return "Domestic";
            case INTERNATIONAL:
                return "International";
            default:
                return "";
        }
    }
    
    private String value;
    
    Scopes(String value)
    {
        this.value = value;
    }
    
    public static Scopes fromValue(String v)
    {
        for (Scopes c : Scopes.values())
        {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
