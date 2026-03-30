package com.app.cinx.api.dto;

import java.util.List;

public class CreateAssignmentSubmissionRequest {
    private String content;
    public String getContent() { return content; }
    public void setContent(String val) { this.content = val; }

    private List<AttachmentRequest> attachments;
    public List<AttachmentRequest> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentRequest> val) { this.attachments = val; }

}