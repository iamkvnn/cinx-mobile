package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateCourseRequest {
    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("categoryId")
    private String categoryId;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @SerializedName("instructorId")
    private String instructorId;

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    @SerializedName("price")
    private Long price;

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @SerializedName("discountedPrice")
    private Long discountedPrice;

    public Long getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Long discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @SerializedName("isPublished")
    private Boolean isPublished;

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    @SerializedName("isInSubscription")
    private Boolean isInSubscription;

    public Boolean getIsInSubscription() {
        return isInSubscription;
    }

    public void setIsInSubscription(Boolean isInSubscription) {
        this.isInSubscription = isInSubscription;
    }

    @SerializedName("duration")
    private Long duration;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @SerializedName("sections")
    private List<CreateSectionRequest> sections;

    public List<CreateSectionRequest> getSections() {
        return sections;
    }

    public void setSections(List<CreateSectionRequest> sections) {
        this.sections = sections;
    }

    // mock field preserved for ui consistency
}
