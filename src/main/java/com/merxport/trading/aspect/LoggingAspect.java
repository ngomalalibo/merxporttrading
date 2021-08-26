package com.merxport.trading.aspect;

import com.merxport.trading.entities.PersistingBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class LoggingAspect
{
    /**
     * This aspect can be used to implement cross cutting concerns like security, logging, caching, emailing etc. The advice is executed along with the Join Point.
     * In this case it performs logging
     */
    
    @Around("@annotation(Loggable)")
    public <T extends PersistingBaseEntity> T logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable
    {
        T pbe = (T) joinPoint.getArgs()[0];
        pbe.auditLog();
        joinPoint.getArgs()[0] = pbe;
        System.out.println("Logged PBE Entity class: " + joinPoint.getArgs()[0].getClass());
        log.info("Logged user audit via aspect");
        pbe = (T) joinPoint.proceed(joinPoint.getArgs());
        System.out.println("Logged Entity class: " + pbe.getClass());
        return pbe; // returns proceeding method calls initial return
    }
    
    @AfterThrowing(value = "@annotation(Loggable)", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Throwable exception)
    {
        log.info("Cause: {}, Message: {}", exception.getCause(), exception.getMessage());
        log.info("Logged via aspect after throwing");
    }
}
