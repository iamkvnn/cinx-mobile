package com.app.cinx.api.dto;

import java.util.List;

public class VerifyEmailRequest {
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String otp;
    public String getOtp() { return otp; }
    public void setOtp(String val) { this.otp = val; }

}