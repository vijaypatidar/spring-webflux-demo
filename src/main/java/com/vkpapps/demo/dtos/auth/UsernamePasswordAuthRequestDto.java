package com.vkpapps.demo.dtos.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UsernamePasswordAuthRequestDto {
    @NotBlank
    @Size(min = 8, max = 12)
    private String username;
    @NotBlank
    @Size(min = 8, max = 16, message = "Password must contains 8 chars")
    private String password;
}
