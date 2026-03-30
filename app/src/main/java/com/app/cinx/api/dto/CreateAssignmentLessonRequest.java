package com.app.cinx.api.dto;

import java.util.List;

public class CreateAssignmentLessonRequest {
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private String startDate;
    public String getStartDate() { return startDate; }
    public void setStartDate(String val) { this.startDate = val; }

    private String dueDate;
    public String getDueDate() { return dueDate; }
    public void setDueDate(String val) { this.dueDate = val; }

    private List<AttachmentDto> attachments;
    public List<AttachmentDto> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentDto> val) { this.attachments = val; }

}