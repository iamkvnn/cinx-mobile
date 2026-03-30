package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddToCartRequest {
    @SerializedName("courseId")
    private String courseId;

    public AddToCartRequest() {}
    
    public AddToCartRequest(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // mock field preserved for ui consistency
}
