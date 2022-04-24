package com.web.security.authentication;

import com.web.security.ValidationException;
import com.web.security.request.LoginRequest;
import com.web.security.request.RegisterRequest;
import com.web.security.role.Role;
import com.web.security.role.RoleRepository;
import com.web.security.role.RoleType;
import com.web.security.user.User;
import com.web.security.user.UserCreator;
import com.web.security.user.UserDetailsImpl;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import com.web.security.verification.*;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.argThat;

@RunWith(SpringRunner.class)
public class AuthenticationControllerTest
{
    public static final String PASSWORD = "password";
    public static final String USERNAME = "user@email.com";

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private JsonWebTokenUtility jsonWebTokenUtility;
    private AuthenticationController authenticationController;
    private UserCreator userCreator;
    private VerificationService verificationService;
    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailService;
    private VerificationToken verificationToken;
    private User user;

    @BeforeEach
    public void before()
    {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        jsonWebTokenUtility = Mockito.mock(JsonWebTokenUtility.class);
        userCreator = Mockito.mock(UserCreator.class);
        verificationService = Mockito.mock(VerificationService.class);
        verificationTokenRepository = Mockito.mock(VerificationTokenRepository.class);
        emailService = Mockito.mock(EmailService.class);
        authenticationController = new AuthenticationController(authenticationManager,
                userRepository,
                roleRepository,
                encoder,
                jsonWebTokenUtility,
                userCreator,
                verificationService,
                verificationTokenRepository,
                emailService);

        verificationToken = Mockito.mock(VerificationToken.class);
        Mockito.when(verificationService.createVerificationToken(Mockito.any())).thenReturn(verificationToken);

        user = new User(USERNAME, PASSWORD);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);

        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void whenNewUserTriesToRegister_thenPasswordIsEncrypted()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);

        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);
        Mockito.verify(encoder).encode(PASSWORD);
    }

    @Test
    public void whenAlreadyRegisteredUsernameIsUsed_thenRegistrationProcessReturnsBadRequest()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        RegisterRequest request = createArtificialSignupRequest();
        Assertions.assertThatThrownBy(() -> authenticationController.registerUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email is already taken!");
    }

    @Test
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.of(role));

        RegisterRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertThat(user.getRoles().size()).isEqualTo(1);

        Set<Role> roles = user.getRoles();
        RoleType actualRoleType = roles.stream().toList().get(0).getName();

        Assertions.assertThat(actualRoleType).isEqualTo(RoleType.ROLE_USER);
    }

    @Test
    public void whenUserTriesToSignIn_thenNewJWTTokenIsGenerated()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        user.setVerified(true);
        authenticationController.authenticateUser(request);

        Mockito.verify(jsonWebTokenUtility).generateJwtToken(authentication);
    }

    @Test
    public void whenUserTriesToSignIn_thenSecurityContextIsProperlySet()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        user.setVerified(true);

        authenticationController.authenticateUser(request);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
    }

    @Test
    public void whenUserTriesToSignIn_thenSetOfGrantedAuthoritiesIsUsed() throws IOException
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = UserDetailsImpl.build(createArtificialUser());
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        user.setVerified(true);

        ResponseEntity<?> response = authenticationController.authenticateUser(request);
        String body = new ObjectMapper().writeValueAsString(response.getBody());
        Assertions.assertThat(body).contains(RoleType.ROLE_USER.name());
    }

    @Test
    public void whenNotRegisteredUserTriesToSignIn_thenAuthenticationFails()
    {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("Bad Credentials Exception"));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        Assertions.assertThatThrownBy(() -> authenticationController.authenticateUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email or password is invalid!");
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsNotVerified()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn((user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        authenticationController.registerUser(request);

        Assertions.assertThat(user.isVerified()).isFalse();
    }

    @Test
    public void whenNewUserTriesToRegister_thenVerificationCodeIsCreatedAndVerificationEmailIsSent()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        RegisterRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn((user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);
        Mockito.verify(verificationService).createVerificationToken(user);
        Mockito.verify(emailService).sendEmail(Mockito.any(EmailContext.class));
    }

    @Test
    public void whenUserTriesToVerifyAccountWithValidToken_thenAccountIsVerified()
    {
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().minusHours(1), user);
        Mockito.when(verificationService.isVerificationTokenValid(token.getToken())).thenReturn(true);
        Mockito.when(verificationTokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        authenticationController.verifyUserAccount(token.getToken());

        Mockito.verify(verificationService).isVerificationTokenValid(token.getToken());
        Mockito.verify(userRepository).save(argThat(User::isVerified));
    }

    @Test
    public void whenUserTriesToVerifyAccountWithInvalidToken_thenAccountIsNotVerified()
    {
        VerificationToken token = new VerificationToken("verification_token_value", LocalDateTime.now().minusHours(1), user);
        Mockito.when(verificationService.isVerificationTokenValid(token.getToken())).thenReturn(false);

        authenticationController.verifyUserAccount(token.getToken());

        Mockito.verify(verificationService).isVerificationTokenValid(token.getToken());
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    public void whenUserAccountIsVerified_thenJwtTokenForUserIsGenerated()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        user.setVerified(true);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        authenticationController.authenticateUser(request);

        Mockito.verify(jsonWebTokenUtility).generateJwtToken(authentication);
    }

    @Test
    public void whenUserAccountIsNotVerified_thenNoJwtTokenIsGenerated()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        user.setVerified(false);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

        LoginRequest request = Mockito.mock(LoginRequest.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Assertions.assertThatThrownBy(() -> authenticationController.authenticateUser(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The user account is not verified!");

        Mockito.verify(jsonWebTokenUtility, Mockito.times(0)).generateJwtToken(authentication);
    }

    private RegisterRequest createArtificialSignupRequest()
    {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        return request;
    }

    private User createArtificialUser()
    {
        User user = new User(USERNAME, PASSWORD);
        user.setRoles(Set.of(new Role(RoleType.ROLE_USER)));
        return user;
    }
}