package com.merxport.trading.security;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserPrincipal implements UserDetails
{
    @Autowired
    private User user;
    
    public UserPrincipal(User user)
    {
        super();
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        for (UserRole r : this.user.getUserRoles())
        {
            grantedAuthorities.add(new SimpleGrantedAuthority(r.getValue()));
        }
        
        return grantedAuthorities;
    }
    
    @Override
    public String getPassword()
    {
        return this.user.getPassword();
    }
    
    @Override
    public String getUsername()
    {
        return this.user.getEmail();
    }
    
    @Override
    public boolean isAccountNonExpired()
    {
        return this.user.isActive();
    }
    
    @Override
    public boolean isAccountNonLocked()
    {
        return this.user.isActive();
    }
    
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }
    
    @Override
    public boolean isEnabled()
    {
        return this.user.isActive();
    }
}
