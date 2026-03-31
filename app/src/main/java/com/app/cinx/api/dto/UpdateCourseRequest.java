package com.app.cinx.api.dto;

import java.util.List;

public class UpdateCourseRequest {
    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private String categoryId;
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String val) { this.categoryId = val; }

    private Long price;
    public Long getPrice() { return price; }
    public void setPrice(Long val) { this.price = val; }

    private Long discountedPrice;
    public Long getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Long val) { this.discountedPrice = val; }

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

    private List<UpdateSectionRequest> sections;
    public List<UpdateSectionRequest> getSections() { return sections; }
    public void setSections(List<UpdateSectionRequest> val) { this.sections = val; }

}