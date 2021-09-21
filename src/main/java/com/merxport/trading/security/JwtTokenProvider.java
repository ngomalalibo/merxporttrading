package com.merxport.trading.security;

import com.merxport.trading.controllers.AuthenticationController;
import com.merxport.trading.entities.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class JwtTokenProvider
{
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length}")// 100hrs
    private Long validityInMilliseconds;
    
    @Autowired
    private UsersDP userDetailsService;
    
    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    public String createToken(User user)
    {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("password", user.getPassword());
        claims.put("roles", user.getUserRoles());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
                   .setClaims(claims)
                   .setIssuedAt(now)
                   // .setExpiration(validity)
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }
    
    
    public Authentication getAuthentication(String token, HttpServletRequest req)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        // log.info("Username {}", userDetails.getUsername());
        // log.info("Password {}", userDetails.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        return usernamePasswordAuthenticationToken;
        // return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }
    
    public String getUsername(String token)
    {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    
    public boolean validateToken(String token) throws JwtException
    {
        try
        {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            // remove token expiry.
        /*if (claims.getBody().getExpiration().before(new Date(System.currentTimeMillis() + validityInMilliseconds)))
        {
            return false;
        }*/
            return true;
        }
        catch (SignatureException e)
        {
            log.error("Invalid JWT signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            log.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            log.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        
        return true;
    }
    
    public String getUsernameFromToken(String token)
    {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        if (!Objects.isNull(claims))
        {
            return claims.getSubject();
        }
        throw new RequestRejectedException("Invalid Token");
    }
    
    
    public String doGenerateRefreshToken(Map<String, Object> claims, String subject)
    {
        
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                   .signWith(SignatureAlgorithm.HS512, secretKey).compact();
        
    }
    
    private String extractJwtFromRequestParam(HttpServletRequest request)
    {
        String token = request.getParameter("token");
        if (token != null && validateToken(token))
        {
            Authentication auth = getAuthentication(token, request);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        /*String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7, bearerToken.length());
        }*/
        return token;
    }
    
    public Claims extractAllClaims(String token)
    {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    
    public String getTokenFromRequestHeader(HttpServletRequest request) throws ServletException, IOException
    {
        String token = null;
        final String authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer "))
        {
            token = authorizationHeaderValue.substring(7);
            
        }
        if (token == null)
        {
            throw new AccessDeniedException("Provide a valid token");
        }
        return token;
    }
    
    public HttpEntity getAuthorizationHeaderToken()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AuthenticationController.TOKEN);
        return new HttpEntity<String>(null, headers);
    }
}
