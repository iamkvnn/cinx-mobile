package com.app.cinx.api.dto;

import java.util.List;

public class ReviewReportResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String reviewId;
    public String getReviewId() { return reviewId; }
    public void setReviewId(String val) { this.reviewId = val; }

    private String reporterId;
    public String getReporterId() { return reporterId; }
    public void setReporterId(String val) { this.reporterId = val; }

    private String reason;
    public String getReason() { return reason; }
    public void setReason(String val) { this.reason = val; }

}