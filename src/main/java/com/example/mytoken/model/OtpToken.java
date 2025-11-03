package com.example.mytoken.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Entity(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 16, max = 16)
    private String otp;

    private LocalDateTime expiryTime;
}
