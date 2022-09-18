package com.vkpapps.demo.models.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UsernamePasswordAuthRequest {
    @NotBlank
    @Size(min = 8)
    private String username;
    @NotBlank
    @Size(min = 8)
    private String password;
}
