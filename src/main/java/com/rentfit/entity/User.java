
package com.rentfit.entity;

import java.time.LocalDateTime;
import com.rentfit.enums.Role;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    private LocalDate dob;

    private String gender;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
