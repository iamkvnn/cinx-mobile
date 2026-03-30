package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SendOtpRequest {
    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // mock field preserved for ui consistency
}
