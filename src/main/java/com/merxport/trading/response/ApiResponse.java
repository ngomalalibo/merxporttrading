package com.merxport.trading.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * This ApiResponse class is used to provide a structured response to the  API client. It contains the responseBody along with the HttpStatus message.
 */
@Data
public class ApiResponse
{
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private Object responseBody;
    
    public ApiResponse(HttpStatus status, String message, Object responseBody)
    {
        super();
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.responseBody = responseBody;
    }
}
