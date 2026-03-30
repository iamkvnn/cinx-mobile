package com.app.cinx.api.dto;

import java.util.List;

public class ResetPasswordRequest {
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String otp;
    public String getOtp() { return otp; }
    public void setOtp(String val) { this.otp = val; }

    private String newPassword;
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String val) { this.newPassword = val; }

}