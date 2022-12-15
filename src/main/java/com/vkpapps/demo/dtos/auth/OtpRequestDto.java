package com.vkpapps.demo.dtos.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpRequestDto {
    @NotBlank
    @Size(min = 8, max = 12)
    private String username;
}
