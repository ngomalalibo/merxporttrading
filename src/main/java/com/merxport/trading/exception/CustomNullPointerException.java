package com.merxport.trading.exception;

public class CustomNullPointerException extends NullPointerException
{
    public CustomNullPointerException()
    {
        super();
    }
    
    public CustomNullPointerException(String s)
    {
        super(s);
    }
}
