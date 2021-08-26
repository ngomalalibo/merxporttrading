package com.merxport.trading.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Qualifier("getObjectMapper")
    @Autowired
    ObjectMapper objectMapper;
    
    // if request path is /user then skip authentication and create user
    @SneakyThrows
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        String requestURI = request.getRequestURI();
        if ("/user".equals(requestURI) || "/upload".equals(requestURI) || "/auth".equals(requestURI) || requestURI.startsWith("/user/verify") || requestURI.contains("/test"))
        {
            log.info("Excluding {} from filter", requestURI);
            return true;
            
        }
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException
    {
        System.out.println("In filter>>>>>>>>>>>>");
        
        String token = req.getParameter("token");
        if (token != null && jwtTokenProvider.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            Authentication auth = jwtTokenProvider.getAuthentication(token, req);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        else
        {
            throw new AccessDeniedException("Provide a valid token");
        }
        
        // HttpServletRequest httpServletRequest = HttpServletRequest.class.cast(servletRequest);
        filterChain.doFilter(req, res);
    }
}
