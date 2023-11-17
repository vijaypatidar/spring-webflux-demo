package com.vkpapps.demo.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpResponseDto {
  private String otpRequestId;
}
