package com.rentfit.service;

import com.rentfit.dto.LoginRequest;
import com.rentfit.dto.LoginResponse;
import com.rentfit.dto.RegisterRequest;
import com.rentfit.dto.RegisterResponse;
import com.rentfit.entity.User;
import com.rentfit.enums.Role;
import com.rentfit.exception.ResourceAlreadyExistException;
import com.rentfit.exception.ResourceNotFoundException;
import com.rentfit.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, OtpService otpService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String email = loginRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email "+email));

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        String email = registerRequest.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email))
            throw new ResourceAlreadyExistException("user with email " + email + " already exists");

        if (!otpService.consumeVerifiedEmail(email)) {
            throw new RuntimeException("Please verify your email using OTP first");
        }

        User user = new User();
        user.setName(registerRequest.getName().trim());
        user.setEmail(email);

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        user.setStatus("ACTIVE");
        user.setVerified(false);

        userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setId(user.getId());
        return response;
    }
}
