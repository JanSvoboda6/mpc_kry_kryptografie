package com.web.security.verification;

import com.web.security.user.User;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerificationService
{
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(16);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private VerificationTokenRepository repository;

    @Autowired
    public VerificationService(VerificationTokenRepository repository)
    {
        this.repository = repository;
    }

    public VerificationToken createVerificationToken(User user)
    {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        VerificationToken token = new VerificationToken(tokenValue, LocalDateTime.now().plusHours(1), user);
        return repository.save(token);
    }

    public boolean isVerificationTokenValid(String token)
    {
        Optional<VerificationToken> tokenOptional = repository.findByToken(token);
        if(tokenOptional.isPresent())
        {
            return tokenOptional.get().getExpiration().isAfter(LocalDateTime.now());
        };
        return false;
    }
}
