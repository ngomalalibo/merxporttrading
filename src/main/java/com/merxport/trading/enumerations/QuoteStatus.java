package com.merxport.trading.enumerations;

public enum QuoteStatus
{
    PENDING("Pending"), ACCEPTED("Accepted"), CHECKED_OUT("Checked Out"), IN_WAREHOUSE("In Warehouse"), IN_TRANSIT("In Transit"), DELIVERED("Delivered"), UNKNOWN("Unknown"), EXPIRED("Expired");
    
    public static String getDisplayText(QuoteStatus i)
    {
        switch (i)
        {
            case PENDING:
                return "Pending";
            case ACCEPTED:
                return "Accepted";
            case CHECKED_OUT:
                return "Checked Out";
            case IN_WAREHOUSE:
                return "In Warehouse";
            case IN_TRANSIT:
                return "In Transit";
            case DELIVERED:
                return "Delivered";
            case UNKNOWN:
                return "Unknown";
            case EXPIRED:
                return "Expired";
            default:
                return "";
        }
    }
    
    private String value;
    
    QuoteStatus(String value)
    {
        this.value = value;
    }
    
    public static QuoteStatus fromValue(String v)
    {
        for (QuoteStatus c : QuoteStatus.values())
        {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
