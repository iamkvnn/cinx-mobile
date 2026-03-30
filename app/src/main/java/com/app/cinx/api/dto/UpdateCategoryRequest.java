package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpdateCategoryRequest {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // mock field preserved for ui consistency
}
