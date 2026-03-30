package com.app.cinx.api.dto;

import java.util.List;

public class AttachmentRequest {
    private String fileKey;
    public String getFileKey() { return fileKey; }
    public void setFileKey(String val) { this.fileKey = val; }

    private String fileName;
    public String getFileName() { return fileName; }
    public void setFileName(String val) { this.fileName = val; }

    private String fileType;
    public String getFileType() { return fileType; }
    public void setFileType(String val) { this.fileType = val; }

    private Long fileSize;
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long val) { this.fileSize = val; }

}