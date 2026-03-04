package com.app.cinx.model;

/**
 * Extended voucher model used in the user's Voucher Wallet screen.
 * Keeps the original {@link Voucher} as-is and adds fields needed for display.
 */
public class ProfileVoucher {

    public enum DiscountType { PERCENT, FIXED }
    public enum Status       { AVAILABLE, EXPIRED, USED }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final String       id;
    private final String       title;
    private final String       description;
    private final String       code;
    private final DiscountType discountType;
    private final int          discountValue;  // percent OR fixed VND amount (in thousands)
    private final String       expiryDate;     // display string e.g. "30/06/2026"
    private final Status       status;
    private final int          quantity;       // remaining uses (0 = unlimited display)
    private final int          cardColorRes;   // background color resource for left ticket panel

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public ProfileVoucher(String id, String title, String description, String code,
                          DiscountType discountType, int discountValue,
                          String expiryDate, Status status,
                          int quantity, int cardColorRes) {
        this.id            = id;
        this.title         = title;
        this.description   = description;
        this.code          = code;
        this.discountType  = discountType;
        this.discountValue = discountValue;
        this.expiryDate    = expiryDate;
        this.status        = status;
        this.quantity      = quantity;
        this.cardColorRes  = cardColorRes;
    }

    // ─────────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────────

    public String       getId()            { return id; }
    public String       getTitle()         { return title; }
    public String       getDescription()   { return description; }
    public String       getCode()          { return code; }
    public DiscountType getDiscountType()  { return discountType; }
    public int          getDiscountValue() { return discountValue; }
    public String       getExpiryDate()    { return expiryDate; }
    public Status       getStatus()        { return status; }
    public int          getQuantity()      { return quantity; }
    public int          getCardColorRes()  { return cardColorRes; }

    /** Human-readable discount label for the ticket left panel. */
    public String getDiscountLabel() {
        if (discountType == DiscountType.PERCENT) {
            return discountValue + "%";
        } else {
            return discountValue + "K";
        }
    }

    /** Sub-label below the discount value. */
    public String getDiscountSubLabel() {
        return discountType == DiscountType.PERCENT ? "GIẢM GIÁ" : "VOUCHER";
    }
}
