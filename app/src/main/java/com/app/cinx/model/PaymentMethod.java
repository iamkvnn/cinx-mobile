package com.app.cinx.model;

import androidx.annotation.DrawableRes;

/**
 * Represents a selectable payment method on the Checkout screen.
 *
 * Fields are kept deliberately minimal — extend with extra metadata
 * (e.g. fee percentage, availability flag) without breaking callers.
 */
public class PaymentMethod {

    public enum Type { MOMO, VNPAY, CARD }

    // ─────────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────────

    private final Type   type;
    private final String name;
    private final String description;

    /** Drawable resource for the icon rendered inside the coloured square. */
    @DrawableRes
    private final int iconRes;

    /** Drawable resource for the coloured square background. */
    @DrawableRes
    private final int iconBgRes;

    // ─────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────

    public PaymentMethod(Type type, String name, String description,
                         @DrawableRes int iconRes, @DrawableRes int iconBgRes) {
        this.type        = type;
        this.name        = name;
        this.description = description;
        this.iconRes     = iconRes;
        this.iconBgRes   = iconBgRes;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Getters (no setters — model is immutable by design)
    // ─────────────────────────────────────────────────────────────────────

    public Type   getType()        { return type; }
    public String getName()        { return name; }
    public String getDescription() { return description; }

    @DrawableRes
    public int getIconRes()   { return iconRes; }

    @DrawableRes
    public int getIconBgRes() { return iconBgRes; }
}
