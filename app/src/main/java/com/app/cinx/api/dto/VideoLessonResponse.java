package com.app.cinx.api.dto;

import java.util.List;

public class VideoLessonResponse {
    private String videoUrl;
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String val) { this.videoUrl = val; }

    private String fileName;
    public String getFileName() { return fileName; }
    public void setFileName(String val) { this.fileName = val; }

    private String fileType;
    public String getFileType() { return fileType; }
    public void setFileType(String val) { this.fileType = val; }

    private Long fileSize;
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long val) { this.fileSize = val; }

    private Integer duration;
    public Integer getDuration() { return duration; }
    public void setDuration(Integer val) { this.duration = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

}