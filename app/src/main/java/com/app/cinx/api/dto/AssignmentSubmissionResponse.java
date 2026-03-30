package com.app.cinx.api.dto;

import java.util.List;

public class AssignmentSubmissionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String submissionTime;
    public String getSubmissionTime() { return submissionTime; }
    public void setSubmissionTime(String val) { this.submissionTime = val; }

    private String assignmentId;
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String val) { this.assignmentId = val; }

    private String content;
    public String getContent() { return content; }
    public void setContent(String val) { this.content = val; }

    private Double score;
    public Double getScore() { return score; }
    public void setScore(Double val) { this.score = val; }

    private String feedback;
    public String getFeedback() { return feedback; }
    public void setFeedback(String val) { this.feedback = val; }

    private List<AssignmentSubmissionAttachmentResponse> attachments;
    public List<AssignmentSubmissionAttachmentResponse> getAttachments() { return attachments; }
    public void setAttachments(List<AssignmentSubmissionAttachmentResponse> val) { this.attachments = val; }

}