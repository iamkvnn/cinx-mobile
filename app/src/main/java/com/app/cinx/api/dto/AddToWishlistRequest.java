package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddToWishlistRequest {
    @SerializedName("courseId")
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // mock field preserved for ui consistency
}
