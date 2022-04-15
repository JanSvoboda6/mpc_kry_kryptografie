package com.web.security.authentication;

import com.web.security.request.LoginRequest;
import com.web.security.request.SignupRequest;
import com.web.security.response.JwtResponse;
import com.web.security.response.MessageResponse;
import com.web.security.role.Role;
import com.web.security.role.RoleRepository;
import com.web.security.role.RoleType;
import com.web.security.user.User;
import com.web.security.user.UserCreator;
import com.web.security.user.UserDetailsImpl;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import com.web.security.verification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
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
    private String fromEmail;

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
        this.fromEmail = fromEmail;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
    {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        User user = userCreator.createUser(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        sendVerificationEmailToUser(savedUser);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private void sendVerificationEmailToUser(User savedUser)
    {
        VerificationToken verificationToken = verificationService.createVerificationToken(savedUser);
        EmailContext emailContext = new EmailContext();
        emailContext.setFrom(fromEmail);
        emailContext.setTo(savedUser.getUsername());
        emailContext.setTemplateLocation("resources/email/templates/verification.html");
        Context context = new Context();
        context.setVariable("link", "http://localhost:8081/api/auth/verification?token=" + verificationToken.getToken());
        emailContext.setContext(context);
        emailContext.setSubject("Dear user, Please activate your account.");
        emailService.sendEmail(emailContext);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        Authentication authentication;
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) //TODO Jan: Test this case
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Bad credentials!"));
        }

        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if(!user.get().isVerified())
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("The user account is not verified!"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jsonWebTokenUtility.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwtToken, userDetails.getId(), userDetails.getUsername(), roles));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> verifyUserAccount(@RequestParam("token") String token)
    {
        if(verificationService.isVerificationTokenValid(token))
        {
            Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
            if(verificationTokenOptional.isPresent())
            {
                User user = verificationTokenOptional.get().getUser();
                user.setVerified(true);
                userRepository.save(user);
                return ResponseEntity.ok("The user account has been verified!");
            }
        }
        return ResponseEntity.badRequest().body("The user account cannot be verified! Please check validity of a token.");
    }

}
