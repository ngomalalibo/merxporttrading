package com.merxport.trading.exception;

import com.merxport.trading.response.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller advice is used to catch the exceptions defined here throughout the application.
 */
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request)
    {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors())
        {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors())
        {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        
        ApiException apiResponse =
                new ApiException(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        
        return buildResponseEntity(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex)
    {
        ApiException apiResponse =
                new ApiException(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return buildResponseEntity(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerExceptionInternal(
            NullPointerException ex)
    {
        ApiException apiResponse =
                new ApiException(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return buildResponseEntity(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOExceptionInternal(
            IOException ex)
    {
        ApiException apiResponse =
                new ApiException(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return buildResponseEntity(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(CustomNullPointerException.class)
    protected ResponseEntity<Object> handleCustomNullPointerExceptionInternal(
            CustomNullPointerException ex)
    {
        ApiException apiResponse =
                new ApiException(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), ex.getMessage());
        return buildResponseEntity(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NonUniqueResultException.class)
    protected ResponseEntity<Object> handleNonUniqueResultExceptionInternal(
            NonUniqueResultException ex)
    {
        ApiException apiResponse =
                new ApiException(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), ex.getMessage());
        return buildResponseEntity(apiResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request)
    {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations())
        {
            errors.add(violation.getRootBeanClass().getName() + " " +
                               violation.getPropertyPath() + ": " + violation.getMessage());
        }
        
        ApiException apiResponse =
                new ApiException(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return buildResponseEntity(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request)
    {
        String error =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();
        
        ApiException apiResponse =
                new ApiException(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return buildResponseEntity(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
        
        ApiException apiResponse = new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                                                    ex.getLocalizedMessage(), builder.toString());
        return buildResponseEntity(apiResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request)
    {
        String template = "Missing parameter:  %s. Missing parameter: %s";
        ApiException apiResponse =
                new ApiException(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), String.format(template, ex.getMessage(), ex.getParameter()));
        return handleExceptionInternal(
                ex, apiResponse, headers, apiResponse.getStatus(), request);
    }
    
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request)
    {
        String error = ex.getParameterName() + " parameter is missing";
        
        ApiException apiResponse =
                new ApiException(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return buildResponseEntity(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex)
    {
        ApiException apiResponse = new ApiException(
                HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), "Provide valid key");
        return buildResponseEntity(apiResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request)
    {
        ApiException apiResponse = new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        return buildResponseEntity(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ResponseEntity<Object> buildResponseEntity(ApiException apiResponse, HttpStatus status)
    {
        return new ResponseEntity<Object>(apiResponse, new HttpHeaders(), status);
    }
}
