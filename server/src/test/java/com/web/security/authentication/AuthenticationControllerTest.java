package com.web.security.authentication;

import com.web.security.request.LoginRequest;
import com.web.security.request.SignupRequest;
import com.web.security.role.Role;
import com.web.security.role.RoleRepository;
import com.web.security.role.RoleType;
import com.web.security.user.User;
import com.web.security.user.UserCreator;
import com.web.security.user.UserDetailsImpl;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import com.web.security.verification.EmailContext;
import com.web.security.verification.EmailService;
import com.web.security.verification.VerificationService;
import com.web.security.verification.VerificationToken;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;

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
    private EmailService emailService;
    private VerificationToken verificationToken;

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
        emailService = Mockito.mock(EmailService.class);
        authenticationController = new AuthenticationController(authenticationManager,
                userRepository,
                roleRepository,
                encoder,
                jsonWebTokenUtility,
                userCreator,
                verificationService,
                emailService);

        verificationToken = Mockito.mock(VerificationToken.class);
        Mockito.when(verificationService.createVerificationToken(Mockito.any())).thenReturn(verificationToken);
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsRegistered()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
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
        SignupRequest request = createArtificialSignupRequest();
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

        SignupRequest request = createArtificialSignupRequest();
        ResponseEntity<?> responseEntity = authenticationController.registerUser(request);

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenUserIsRegistered_thenUserRoleShouldBeSetOnlyToUser()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.of(role));

        SignupRequest request = createArtificialSignupRequest();
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

        ResponseEntity<?> response = authenticationController.authenticateUser(request);
        String body = new ObjectMapper().writeValueAsString(response.getBody());

        Assertions.assertThat(body).contains(RoleType.ROLE_USER.name());
    }

    @Test
    public void whenNotRegisteredUserTriesToSignIn_thenAuthenticationFails()
    {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("Bad Credentials Exception"));
        LoginRequest request = Mockito.mock(LoginRequest.class);
        ResponseEntity<?> responseEntity = authenticationController.authenticateUser(request);
        int BAD_REQUEST_CODE = 400;
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(BAD_REQUEST_CODE);
    }

    @Test
    public void whenNewUserTriesToRegister_thenUserIsNotVerified()
    {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Role role = Mockito.mock(Role.class);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(java.util.Optional.ofNullable(role));
        SignupRequest request = createArtificialSignupRequest();
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
        SignupRequest request = createArtificialSignupRequest();
        Mockito.when(encoder.encode(request.getPassword())).thenReturn(request.getPassword());
        User user = createArtificialUser();
        Mockito.when(userCreator.createUser(request.getUsername(), request.getPassword())).thenReturn((user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        authenticationController.registerUser(request);
        Mockito.verify(verificationService).createVerificationToken(user);
        Mockito.verify(emailService).sendEmail(Mockito.any(EmailContext.class));
    }

    private SignupRequest createArtificialSignupRequest()
    {
        SignupRequest request = new SignupRequest();
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