package com.app.cinx.api.dto;

import java.util.List;

public class VoucherResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String code;
    public String getCode() { return code; }
    public void setCode(String val) { this.code = val; }

    private Long discountAmount;
    public Long getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Long val) { this.discountAmount = val; }

    private Long minPurchaseAmount;
    public Long getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Long val) { this.minPurchaseAmount = val; }

    private String maxDiscountAmount;
    public String getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(String val) { this.maxDiscountAmount = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private Long quantity;
    public Long getQuantity() { return quantity; }
    public void setQuantity(Long val) { this.quantity = val; }

    private String validFrom;
    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String val) { this.validFrom = val; }

    private String validTo;
    public String getValidTo() { return validTo; }
    public void setValidTo(String val) { this.validTo = val; }

    private String createdAt;
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String val) { this.createdAt = val; }

    private String updatedAt;
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String val) { this.updatedAt = val; }

    private Boolean isSelected = false;
    public Boolean isSelected() { return isSelected != null && isSelected; }
    public void setSelected(Boolean val) { this.isSelected = val; }

}