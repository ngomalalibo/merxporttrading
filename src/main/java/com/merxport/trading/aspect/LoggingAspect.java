package com.merxport.trading.aspect;

import com.merxport.trading.entities.User;
import com.merxport.trading.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Aspect
@Configuration
public class LoggingAspect
{
    @Autowired
    UserRepository userRepository;
    
    /**
     * This aspect can be used to implement cross cutting concerns like security, logging, caching, emailing etc. The advice is executed along with the Join Point.
     * In this case it performs logging
     */
    
    @Around("@annotation(Loggable)")
    public User logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable
    {
        User user;
        User userWithLog;
        if (joinPoint.getArgs()[0] instanceof String)
        {
            user = userRepository.findById((String) joinPoint.getArgs()[0]).orElse(null);
            assert user != null;
            userWithLog = auditLog(user);
            joinPoint.getArgs()[0] = userWithLog.getId();
        }
        else
        {
            user = (User) joinPoint.getArgs()[0];
            userWithLog = auditLog(user);
            joinPoint.getArgs()[0] = userWithLog;
        }
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        log.info("Logged user audit via aspect");
        return (User) proceed; // returns proceeding method calls initial return
    }
    
    @AfterThrowing(value = "@annotation(Loggable)", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception)
    {
        log.info("Cause: {}, Message: {}", exception.getCause(), exception.getMessage());
        log.info("Logged via aspect after throwing");
    }
    
    public User auditLog(User user)
    {
        if (Objects.isNull(user.getAudit().getCreatedDate()))
        {
            user.getAudit().setCreatedBy("System");
            user.getAudit().setCreatedDate(LocalDateTime.now());
        }
        else
        {
            user.getAudit().setModifiedBy("System");
            user.getAudit().setModifiedDate(LocalDateTime.now());
        }
        if (!user.isActive())
        {
            user.getAudit().setArchivedBy("System");
            user.getAudit().setArchivedDate(LocalDateTime.now());
        }
        
        return user;
    }
}
