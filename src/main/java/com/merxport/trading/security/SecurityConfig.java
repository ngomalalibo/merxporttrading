package com.merxport.trading.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private JwtConfigurer jwtConfigurer;
    
    @Autowired
    private UsersDP usersDP;
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }
    
    @Bean
    @Override
    public UserDetailsService userDetailsService()
    {
        return usersDP;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    {
        auth.authenticationProvider(new UserAuthenticationProvider());
    }
    
    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper()
    {
        SimpleAuthorityMapper authMapper = new SimpleAuthorityMapper();
        authMapper.setConvertToUpperCase(true);
        authMapper.setDefaultAuthority("BUYER");
        authMapper.setPrefix("");
        return authMapper;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/user", "/auth", "/test/**", "/upload", "/user/verify/**").permitAll()
                .anyRequest().authenticated().and()
                .apply(jwtConfigurer);
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/user", "/test/**", "/upload", "/auth", "/user/verify/**");
    }
}
