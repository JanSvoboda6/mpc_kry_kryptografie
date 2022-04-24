package com.web.security.user;

import org.springframework.stereotype.Service;

/**
 * Helper class for creating the user.
 */
@Service
public class UserCreator
{
    public User createUser(String username, String password)
    {
        return new User(username, password);
    }
}
