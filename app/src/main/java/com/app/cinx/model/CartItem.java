package com.app.cinx.model;

/**
 * Represents a single course item inside the shopping cart.
 *
 * This model is intentionally decoupled from {@link Course} so that
 * cart-specific state (selected, original/sale price) can evolve
 * independently of the catalogue model.
 */
public class CartItem {

    private final int    id;
    private final String title;
    private final String instructor;
    private final long   originalPrice;   // e.g. 1_200_000
    private final long   salePrice;       // e.g.   599_000
    private final String imageUrl;
    private final String category;

    /** Whether the checkbox next to this item is ticked by the user. */
    private boolean selected;

    // ─────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────

    public CartItem(int id,
                    String title,
                    String instructor,
                    long originalPrice,
                    long salePrice,
                    String imageUrl,
                    String category) {
        this.id            = id;
        this.title         = title;
        this.instructor    = instructor;
        this.originalPrice = originalPrice;
        this.salePrice     = salePrice;
        this.imageUrl      = imageUrl;
        this.category      = category;
        this.selected      = false;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Getters / Setters
    // ─────────────────────────────────────────────────────────────────────

    public int    getId()            { return id; }
    public String getTitle()         { return title; }
    public String getInstructor()    { return instructor; }
    public long   getOriginalPrice() { return originalPrice; }
    public long   getSalePrice()     { return salePrice; }
    public String getImageUrl()      { return imageUrl; }
    public String getCategory()      { return category; }

    public boolean isSelected()                  { return selected; }
    public void    setSelected(boolean selected) { this.selected = selected; }

    // Convenience: how much the user saves on this item
    public long getSavings() {
        return originalPrice - salePrice;
    }
}
