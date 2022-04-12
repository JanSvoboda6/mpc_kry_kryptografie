package com.web.security.configuration;

import com.web.security.authentication.AuthorizationTokenFilter;
import com.web.security.authentication.AuthenticationEntryPointJwt;
import com.web.security.user.UserDetailsServiceImpl;
import com.web.security.utility.JsonWebTokenUtility;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(JsonWebTokenUtility jsonWebTokenUtility,
                             UserDetailsServiceImpl userDetailsService,
                             AuthenticationEntryPointJwt unauthorizedHandler)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthorizationTokenFilter authenticationJwtTokenFilter()
    {
        return new AuthorizationTokenFilter(jsonWebTokenUtility, userDetailsService);
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TemplateEngine templateEngine()
    {
        return  new TemplateEngine();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
    {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        //TODO Jan: These headers are just for accessing H2 console
        http.authorizeRequests().antMatchers("/console/**").permitAll();
        http.csrf().ignoringAntMatchers("/console/**");
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests().antMatchers("/api/**").permitAll();
        http.csrf().ignoringAntMatchers("/api/**");
        
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}