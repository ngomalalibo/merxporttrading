package com.merxport.trading.enumerations;

import lombok.Getter;

@Getter
public enum UserScopes
{
    DOMESTIC("Domestic"), INTERNATIONAL("International");
    
    public static String getDisplayText(UserScopes i)
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
    
    UserScopes(String value)
    {
        this.value = value;
    }
    
    public static UserScopes fromValue(String v)
    {
        for (UserScopes c : UserScopes.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
