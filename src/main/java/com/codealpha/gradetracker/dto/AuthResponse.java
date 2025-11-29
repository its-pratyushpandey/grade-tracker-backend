package com.codealpha.gradetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String fullName;
    private String role;
    private Long expiresIn;
}
