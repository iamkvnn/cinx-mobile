package com.app.cinx.api.dto;

import java.util.List;

public class AssignmentSubmissionAttachmentResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String fileName;
    public String getFileName() { return fileName; }
    public void setFileName(String val) { this.fileName = val; }

    private String fileType;
    public String getFileType() { return fileType; }
    public void setFileType(String val) { this.fileType = val; }

    private Long fileSize;
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long val) { this.fileSize = val; }

    private String attachmentUrl;
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String val) { this.attachmentUrl = val; }

}