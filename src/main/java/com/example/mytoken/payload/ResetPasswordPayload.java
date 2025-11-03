package com.example.mytoken.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordPayload {

    @NotBlank
    private String email;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
