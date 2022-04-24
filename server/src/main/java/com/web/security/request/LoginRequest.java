package com.web.security.request;

import javax.validation.constraints.NotBlank;

/**
 * Mapping class from HTTP login request to Java object used in {@link com.web.security.authentication.AuthenticationController}.
 */
public class LoginRequest
{
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
