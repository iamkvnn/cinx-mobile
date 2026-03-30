package com.app.cinx.api.dto;

import java.util.List;

public class CreateCourseImageRequest {
    private List<ImageDto> images;
    public List<ImageDto> getImages() { return images; }
    public void setImages(List<ImageDto> val) { this.images = val; }

}