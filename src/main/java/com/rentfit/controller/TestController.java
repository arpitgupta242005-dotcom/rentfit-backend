
package com.rentfit.controller;

import com.rentfit.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1")
@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> publicTest() {
        return ResponseEntity.ok(
                new ApiResponse<>("Test endpoint working", "Test", HttpStatus.OK.value())
        );
    }

    @GetMapping("/test/protected")
    public ResponseEntity<ApiResponse<Map<String, String>>> protectedTest() {
        Map<String, String> data = Map.of(
                "message", "JWT authentication successful"
        );
        return ResponseEntity.ok(
                new ApiResponse<>("Protected endpoint accessed successfully", data, HttpStatus.OK.value())
        );
    }
}