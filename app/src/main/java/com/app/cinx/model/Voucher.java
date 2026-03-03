package com.app.cinx.model;

/**
 * Represents a discount voucher that can be applied at checkout.
 */
public class Voucher {

    private final String id;
    private final String title;
    private final String description;
    private final String code;
    private final int    discountPercent;   // e.g. 20 means 20 %

    public Voucher(String id,
                   String title,
                   String description,
                   String code,
                   int    discountPercent) {
        this.id              = id;
        this.title           = title;
        this.description     = description;
        this.code            = code;
        this.discountPercent = discountPercent;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────────────

    public String getId()              { return id; }
    public String getTitle()           { return title; }
    public String getDescription()     { return description; }
    public String getCode()            { return code; }
    public int    getDiscountPercent() { return discountPercent; }
}
