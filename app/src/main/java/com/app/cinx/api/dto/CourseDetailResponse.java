package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CourseDetailResponse {
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @SerializedName("category")
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @SerializedName("instructor")
    private InstructorResponse instructor;

    public InstructorResponse getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorResponse instructor) {
        this.instructor = instructor;
    }

    @SerializedName("images")
    private List<CourseImageResponse> images;

    public List<CourseImageResponse> getImages() {
        return images;
    }

    public void setImages(List<CourseImageResponse> images) {
        this.images = images;
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

    @SerializedName("discountRate")
    private Long discountRate;

    public Long getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Long discountRate) {
        this.discountRate = discountRate;
    }

    @SerializedName("rating")
    private Double rating;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @SerializedName("enrollmentCount")
    private Long enrollmentCount;

    public Long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
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

    @SerializedName("createdAt")
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @SerializedName("sections")
    private List<SectionResponse> sections;

    public List<SectionResponse> getSections() {
        return sections;
    }

    public void setSections(List<SectionResponse> sections) {
        this.sections = sections;
    }

    // mock field preserved for ui consistency
}
