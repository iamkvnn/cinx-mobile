package com.app.cinx.api.dto;

import java.util.List;

public class ChangeEmailRequest {
    private String oldEmail;
    public String getOldEmail() { return oldEmail; }
    public void setOldEmail(String val) { this.oldEmail = val; }

    private String otp;
    public String getOtp() { return otp; }
    public void setOtp(String val) { this.otp = val; }

    private String newEmail;
    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String val) { this.newEmail = val; }

}