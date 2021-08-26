package com.merxport.trading.exception;

public class DuplicateEntityException extends RuntimeException
{
    public DuplicateEntityException()
    {
        super();
    }
    public DuplicateEntityException(String message)
    {
        super(message);
    }
}
