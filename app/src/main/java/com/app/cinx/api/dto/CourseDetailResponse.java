package com.app.cinx.api.dto;

import java.util.List;

public class CourseDetailResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private String category;
    public String getCategory() { return category; }
    public void setCategory(String val) { this.category = val; }

    private InstructorResponse instructor;
    public InstructorResponse getInstructor() { return instructor; }
    public void setInstructor(InstructorResponse val) { this.instructor = val; }

    private List<CourseImageResponse> images;
    public List<CourseImageResponse> getImages() { return images; }
    public void setImages(List<CourseImageResponse> val) { this.images = val; }

    private Long price;
    public Long getPrice() { return price; }
    public void setPrice(Long val) { this.price = val; }

    private Long discountedPrice;
    public Long getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Long val) { this.discountedPrice = val; }

    private Long discountRate;
    public Long getDiscountRate() { return discountRate; }
    public void setDiscountRate(Long val) { this.discountRate = val; }

    private Double rating;
    public Double getRating() { return rating; }
    public void setRating(Double val) { this.rating = val; }

    private Long enrollmentCount;
    public Long getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(Long val) { this.enrollmentCount = val; }

    private Boolean isPublished;
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean val) { this.isPublished = val; }

    private Boolean isInSubscription;
    public Boolean getIsInSubscription() { return isInSubscription; }
    public void setIsInSubscription(Boolean val) { this.isInSubscription = val; }

    private Long duration;
    public Long getDuration() { return duration; }
    public void setDuration(Long val) { this.duration = val; }

    private Boolean hasCertificate;
    public Boolean getHasCertificate() { return hasCertificate; }
    public void setHasCertificate(Boolean val) { this.hasCertificate = val; }

    private String certificateTitle;
    public String getCertificateTitle() { return certificateTitle; }
    public void setCertificateTitle(String val) { this.certificateTitle = val; }

    private String createdAt;
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String val) { this.createdAt = val; }

    private String updatedAt;
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String val) { this.updatedAt = val; }

    private List<SectionResponse> sections;
    public List<SectionResponse> getSections() { return sections; }
    public void setSections(List<SectionResponse> val) { this.sections = val; }

}