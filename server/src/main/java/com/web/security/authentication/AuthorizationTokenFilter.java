package com.web.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.web.security.utility.JsonWebTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthorizationTokenFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationTokenFilter.class);
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthorizationTokenFilter(JsonWebTokenUtility jsonWebTokenUtility, UserDetailsService userDetailsService)
    {
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        try
        {
            String jwtToken = jsonWebTokenUtility.parseJwt(request);
            if (jwtToken != null && jsonWebTokenUtility.validateJwtToken(jwtToken))
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(jwtToken));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exception)
        {
            logger.error("Cannot set user authentication.", exception);
        }

        filterChain.doFilter(request, response);
    }
}
