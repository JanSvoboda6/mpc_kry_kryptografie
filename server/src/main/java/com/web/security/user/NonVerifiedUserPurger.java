package com.web.security.user;

import com.web.security.verification.VerificationService;
import com.web.security.verification.VerificationToken;
import com.web.security.verification.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class NonVerifiedUserPurger
{
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final VerificationService verificationService;

    @Autowired
    public NonVerifiedUserPurger(UserRepository userRepository, VerificationTokenRepository tokenRepository, VerificationService verificationService)
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.verificationService = verificationService;
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void purge()
    {
        List<User> notVerifiedUsers = userRepository.findAllByIsVerified(false);
        for (User user : notVerifiedUsers)
        {
            Optional<VerificationToken> tokenOptional = tokenRepository.findByUser(user);
            if(tokenOptional.isPresent())
            {
                if(!verificationService.isVerificationTokenValid(tokenOptional.get().getToken()))
                {
                    purgeVerificationTokenAndUser(user);
                }
            }
        }
    }

    private void purgeVerificationTokenAndUser(User user)
    {
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }
}
