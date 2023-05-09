package com.vkpapps.demo.dtos.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OtpRequestDto {
    @NotBlank
    @Size(min = 8, max = 12)
    private String username;
}
