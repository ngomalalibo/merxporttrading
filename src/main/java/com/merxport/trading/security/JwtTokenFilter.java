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
    
    public static final String[] FILTER_WHITELIST = {
            // -- Swagger UI v2
            "/user", "/auth", "/test/", "/upload", "/getImage", "/user/verify/",
            "/api-docs",
            "/v2/api-docs",
            "/configuration/**",
            "/configuration*/**",
            "/swagger*/**",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs",
            "/swagger-ui"
            // other public endpoints of your API may be appended to this array
    };
    
    @Qualifier("getObjectMapper")
    @Autowired
    ObjectMapper objectMapper;
    
    // if request path is /user then skip authentication and create user
    @SneakyThrows
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        String requestURI = request.getRequestURI().toLowerCase();
        for (String uri : FILTER_WHITELIST)
        {
            if (requestURI.startsWith(uri.toLowerCase()))
            {
                log.info("Excluding {} from filter", requestURI);
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException
    {
        // System.out.println("In filter>>>>>>>>>>>>");
        String token = jwtTokenProvider.getTokenFromRequestHeader(req);
        
        if (jwtTokenProvider.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null)
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
