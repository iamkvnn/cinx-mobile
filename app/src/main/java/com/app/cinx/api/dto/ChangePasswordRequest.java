package com.app.cinx.api.dto;

import java.util.List;

public class ChangePasswordRequest {
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String otp;
    public String getOtp() { return otp; }
    public void setOtp(String val) { this.otp = val; }

    private String oldPassword;
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String val) { this.oldPassword = val; }

    private String newPassword;
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String val) { this.newPassword = val; }

}