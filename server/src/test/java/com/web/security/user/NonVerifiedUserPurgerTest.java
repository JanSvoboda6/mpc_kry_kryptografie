package com.web.security.user;

import com.web.security.verification.VerificationToken;
import com.web.security.verification.VerificationTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class NonVerifiedUserPurgerTest
{
    public static final String EMAIL = "user@email.com";
    public static final String PASSWORD = "StrongPassword_999";

    @Autowired
    private NonVerifiedUserPurger purger;

    @Autowired
    private UserRepository userRepository;
    private User user;
    private User savedUser;
    private VerificationToken savedToken;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @BeforeEach
    public void before()
    {
        user = new User(EMAIL, PASSWORD);
    }

    @AfterEach
    public void after()
    {
        tokenRepository.delete(savedToken);
        userRepository.delete(savedUser);
    }

    @Test
    public void whenUserIsNotVerifiedAndVerificationTokenIsExpired_thenUserIsDeleted()
    {
        user.setVerified(false);
        savedUser = userRepository.save(user);
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().minusDays(1), savedUser);
        savedToken = tokenRepository.save(token);

        purger.purge();

        Assertions.assertThat(userRepository.existsByUsername(savedUser.getUsername())).isFalse();
        Assertions.assertThat(tokenRepository.findByUser(savedUser)).isEmpty();
    }

    @Test
    public void whenUserIsNotVerifiedAndVerificationTokenIsNotExpired_thenUserIsNotPurged()
    {
        user.setVerified(false);
        savedUser = userRepository.save(user);
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().plusDays(1), savedUser);
        savedToken = tokenRepository.save(token);

        purger.purge();

        Assertions.assertThat(userRepository.existsByUsername(savedUser.getUsername())).isTrue();
        Assertions.assertThat(tokenRepository.findByUser(savedUser)).isPresent();
    }

    @Test
    public void whenUserIsVerified_thenUserIsNotPurged()
    {
        user.setVerified(true);
        savedUser = userRepository.save(user);
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().plusDays(1), savedUser);
        savedToken = tokenRepository.save(token);

        purger.purge();

        Assertions.assertThat(userRepository.existsByUsername(savedUser.getUsername())).isTrue();
        Assertions.assertThat(tokenRepository.findByUser(savedUser)).isPresent();
    }
}