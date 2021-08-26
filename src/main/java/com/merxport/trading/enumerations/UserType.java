package com.merxport.trading.enumerations;

public enum UserType
{
    PERSONAL("Personal"), BUSINESS("Business");
    
    public static String getDisplayText(UserType i)
    {
        switch (i)
        {
            case PERSONAL:
                return "Personal";
            case BUSINESS:
                return "Business";
            default:
                return "";
        }
    }
    
    private String value;
    
    UserType(String value)
    {
        this.value = value;
    }
    
    public static UserType fromValue(String v)
    {
        for (UserType c : UserType.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
