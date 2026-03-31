package com.app.cinx.api.dto;

import java.util.List;

public class CertificateRequestResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

    private String certificateUrl;
    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String val) { this.certificateUrl = val; }

    private String requestedAt;
    public String getRequestedAt() { return requestedAt; }
    public void setRequestedAt(String val) { this.requestedAt = val; }

    private String approvedAt;
    public String getApprovedAt() { return approvedAt; }
    public void setApprovedAt(String val) { this.approvedAt = val; }

}