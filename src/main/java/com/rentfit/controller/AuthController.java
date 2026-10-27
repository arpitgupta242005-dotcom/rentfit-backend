
package com.rentfit.controller;

import com.rentfit.dto.LoginRequest;
import com.rentfit.dto.RegisterRequest;
import com.rentfit.dto.SendOtpRequest;
import com.rentfit.dto.VerifyOtpRequest;
import com.rentfit.entity.User;
import com.rentfit.enums.Role;
import com.rentfit.repository.UserRepository;
import com.rentfit.service.JwtService;
import com.rentfit.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            OtpService otpService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail());

        return ResponseEntity.ok(
                Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        boolean verified = otpService.verifyOtp(
                request.getEmail(), request.getOtp());

        if (!verified) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(
                Map.of("message", "OTP verified successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {

        String email = request.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "Email is already registered"));
        }
        if (!otpService.consumeVerifiedEmail(email)) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Please verify your email using OTP first"));
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPasswordHash(encodedPassword);
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        user.setStatus("ACTIVE");
        user.setVerified(false);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest request) {

        String email = request.getEmail()
                .trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token,
                "tokenType", "Bearer"));
    }
}
