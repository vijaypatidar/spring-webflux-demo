package com.vkpapps.demo.dtos.auth;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    private String otpRequestId;
    private int otp;
}
