package com.merxport.trading.enumerations;

import lombok.Getter;

@Getter
public enum UserRole
{
    /**
     * User Roles statuses are captured using this enumeration
     */
    BUYER("Buyer"), SELLER("Seller"), ADMIN("Admin");
    
    public static String getDisplayText(UserRole i)
    {
        switch (i)
        {
            case BUYER:
                return "Buyer";
            case SELLER:
                return "Seller";
            case ADMIN:
                return "Admin";
            default:
                return "";
        }
    }
    
    private String value;
    
    UserRole(String value)
    {
        this.value = value;
    }
    
    public static UserRole fromValue(String v)
    {
        for (UserRole c : UserRole.values())
        {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
