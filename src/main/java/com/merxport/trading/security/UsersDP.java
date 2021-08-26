package com.merxport.trading.security;

import com.merxport.trading.entities.User;
import com.merxport.trading.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersDP implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(s);
        
        if (user == null)
        {
            throw new UsernameNotFoundException("user not found: " + s);
        }
        
        return new UserPrincipal(user);
    }
}
