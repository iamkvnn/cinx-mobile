package com.app.cinx.model;

/**
 * Represents a single course inside an {@link Order}.
 */
public class OrderItem {

    private final String title;
    private final String instructor;
    private final String imageUrl;
    private final long   originalPrice;  // VND – original (crossed-out) price
    private final long   salePrice;      // VND – actual paid price

    public OrderItem(String title, String instructor, String imageUrl,
                     long originalPrice, long salePrice) {
        this.title         = title;
        this.instructor    = instructor;
        this.imageUrl      = imageUrl;
        this.originalPrice = originalPrice;
        this.salePrice     = salePrice;
    }

    public String getTitle()         { return title; }
    public String getInstructor()    { return instructor; }
    public String getImageUrl()      { return imageUrl; }
    public long   getOriginalPrice() { return originalPrice; }
    public long   getSalePrice()     { return salePrice; }
}
