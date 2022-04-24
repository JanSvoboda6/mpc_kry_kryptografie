package com.web.security.authentication;

import com.web.security.ValidationException;
import com.web.security.request.LoginRequest;
import com.web.security.request.RegisterRequest;
import com.web.security.response.JwtResponse;
import com.web.security.role.Role;
import com.web.security.role.RoleRepository;
import com.web.security.role.RoleType;
import com.web.security.user.User;
import com.web.security.user.UserCreator;
import com.web.security.user.UserDetailsImpl;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import com.web.security.verification.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller class providing method for login/registration/validation  of {@link User}.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserCreator userCreator;
    private final VerificationService verificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String FROM_EMAIL;

    @Value("${web.backend.api}")
    private String BACKEND_API;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder,
            JsonWebTokenUtility jsonWebTokenUtility,
            UserCreator userCreator,
            VerificationService verificationService,
            VerificationTokenRepository verificationTokenRepository,
            EmailService emailService)
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userCreator = userCreator;
        this.verificationService = verificationService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest)
    {
        LOGGER.info("Register request: {}", signUpRequest.getUsername());
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            throw new ValidationException("Email is already taken!");
        }

        User user = userCreator.createUser(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new ValidationException("Role cannot be found!"));
        roles.add(userRole);
        user.setRoles(roles);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        sendVerificationEmailToUser(savedUser);

        LOGGER.info("Successful registration: {}", signUpRequest.getUsername());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        LOGGER.info("Authentication request: {}", loginRequest.getUsername());
        Authentication authentication;
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception)
        {
            if (exception instanceof DisabledException)
            {
                throw new ValidationException("The user account is not verified!");
            }
            throw new ValidationException("Email or password is invalid!");
        }

        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if(!user.get().isVerified())
        {
            throw new ValidationException("The user account is not verified!");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jsonWebTokenUtility.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        LOGGER.info("Successful authentication: {}", loginRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwtToken, userDetails.getId(), userDetails.getUsername(), roles));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> verifyUserAccount(@RequestParam("token") String token)
    {
        LOGGER.info("Verification request: {}", token);
        if(verificationService.isVerificationTokenValid(token))
        {
            Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
            if(verificationTokenOptional.isPresent())
            {
                User user = verificationTokenOptional.get().getUser();
                user.setVerified(true);
                userRepository.save(user);
                LOGGER.info("Successful verification: {}, {}", user.getUsername(), token);
                return ResponseEntity.ok("The user account has been verified!");
            }
        }
        return ResponseEntity.badRequest().body("The user account cannot be verified! Please check validity of a token.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException exception)
    {
        return ResponseEntity.badRequest().body(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    private void sendVerificationEmailToUser(User user)
    {
        VerificationToken verificationToken = verificationService.createVerificationToken(user);
        EmailContext emailContext = new EmailContext();
        emailContext.setFrom(FROM_EMAIL);
        emailContext.setTo(user.getUsername());
        emailContext.setTemplateLocation("verification");
        Context context = new Context();
        context.setVariable("link", BACKEND_API + "/api/auth/verification?token=" + verificationToken.getToken());
        emailContext.setContext(context);
        emailContext.setSubject("Dear user, please activate your account.");
        emailService.sendEmail(emailContext);
        LOGGER.info("Sending verification email: {}", user.getUsername());
    }
}
