package com.app.cinx.api.dto;

import java.util.List;

public class PresignedUrlResponse {
    private String fileKey;
    public String getFileKey() { return fileKey; }
    public void setFileKey(String val) { this.fileKey = val; }

    private String presignedUrl;
    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String val) { this.presignedUrl = val; }

}