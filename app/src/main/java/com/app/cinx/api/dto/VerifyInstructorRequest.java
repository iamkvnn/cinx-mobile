package com.app.cinx.api.dto;

public class VerifyInstructorRequest {
    private Boolean verified;

    public VerifyInstructorRequest() {
        this.verified = true;
    }

    public VerifyInstructorRequest(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
