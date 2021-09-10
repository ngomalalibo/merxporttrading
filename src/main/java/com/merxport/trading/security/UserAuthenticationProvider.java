package com.merxport.trading.security;

import com.google.common.base.Strings;
import com.merxport.trading.entities.User;
import com.merxport.trading.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserAuthenticationProvider extends DaoAuthenticationProvider
{
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UsersDP userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserAuthenticationProvider()
    {
        super();
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        log.info("authenticating......");
        
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        log.info("authenticate -> " + username);
        log.info("credentials -> " + password);
        
        if (Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(username))
        {
            throw new BadCredentialsException("Invalid username or password.");
        }
        
        User usere = userRepository.findByEmail(username);
        
        if (usere == null)
        {
            throw new BadCredentialsException("User does not exist");
        }
        
        if (passwordEncoder.getPasswordEncoder().matches(password, usere.getPassword()))
        {
            System.out.println("Password Matches");
            Set<SimpleGrantedAuthority> authorities = usere.getUserRoles().stream().map(role -> new SimpleGrantedAuthority(role.getValue())).collect(Collectors.toSet());
            
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }
        else
        {
            throw new BadCredentialsException("Invalid password.");
        }
    }
    
    
    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    @Override
    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper)
    {
        super.setAuthoritiesMapper(new SecurityConfig().authoritiesMapper());
    }
    
    
    @Override
    public void setPasswordEncoder(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder)
    {
        super.setPasswordEncoder(passwordEncoder);
    }
    
    @Autowired
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService)
    {
        super.setUserDetailsService(this.userDetailsService);
    }
    
    /*@Override
    protected void doAfterPropertiesSet()
    {
        if (super.getUserDetailsService() != null)
        {
            System.out.println("UserDetailsService has been configured properly");
        }
    }*/
}
