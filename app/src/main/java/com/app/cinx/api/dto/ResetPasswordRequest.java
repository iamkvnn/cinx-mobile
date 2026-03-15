package com.app.cinx.api.dto;

public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String otp;

    public ResetPasswordRequest(String email, String newPassword, String otp) {
        this.email       = email;
        this.newPassword = newPassword;
        this.otp         = otp;
    }
}
