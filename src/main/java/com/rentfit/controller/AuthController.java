
package com.rentfit.controller;

import com.rentfit.dto.*;
import com.rentfit.dto.response.ApiResponse;
import com.rentfit.service.AuthService;
import com.rentfit.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;

    public AuthController(
            OtpService otpService,
            AuthService authService) {
        this.otpService = otpService;
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>("OTP sent successfully", null, HttpStatus.OK.value()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        boolean verified = otpService.verifyOtp(
                request.getEmail(), request.getOtp());

        if (!verified) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>("Invalid or expired OTP", null, HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("OTP verified successfully", null, HttpStatus.OK.value()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse registerResponse = authService.register(request);

        ApiResponse<RegisterResponse> response = new ApiResponse<>(
                "User registered successfully.",
                registerResponse,
                HttpStatus.CREATED.value()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.rentfit.dto.LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        ApiResponse<LoginResponse> response = new ApiResponse<>(
                "User logged in successfully.",
                loginResponse,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
