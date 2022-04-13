package com.web.security.verification;

import com.web.security.user.User;
import org.springframework.stereotype.Service;

@Service
public class VerificationService
{
    public VerificationToken createVerificationToken(User user)
    {
        return null;
    }

    public boolean isVerificationTokenValid(VerificationToken token)
    {
        return false;
    }
}
