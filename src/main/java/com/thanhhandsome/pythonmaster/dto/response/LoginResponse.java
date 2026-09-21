package com.thanhhandsome.pythonmaster.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    String accessToken;
    String tokenType;
    long expiresIn;   // milliseconds
    String username;
}
