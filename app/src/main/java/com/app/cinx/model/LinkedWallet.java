package com.app.cinx.model;

import androidx.annotation.DrawableRes;

/**
 * Represents a linked e-wallet (MoMo, ZaloPay, VNPAY, etc.).
 */
public class LinkedWallet {

    public enum WalletType { MOMO, VNPAY, ZALOPAY, OTHER }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final String     id;
    private final WalletType type;
    private final String     displayName;
    private final String     maskedPhone;  // e.g. "0912***678"
    @DrawableRes
    private final int        iconRes;
    private final int        iconBgColor;  // background tint

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public LinkedWallet(String id, WalletType type, String displayName,
                        String maskedPhone, @DrawableRes int iconRes, int iconBgColor) {
        this.id          = id;
        this.type        = type;
        this.displayName = displayName;
        this.maskedPhone = maskedPhone;
        this.iconRes     = iconRes;
        this.iconBgColor = iconBgColor;
    }

    // ─────────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────────

    public String     getId()          { return id; }
    public WalletType getType()        { return type; }
    public String     getDisplayName() { return displayName; }
    public String     getMaskedPhone() { return maskedPhone; }
    @DrawableRes public int getIconRes() { return iconRes; }
    public int        getIconBgColor() { return iconBgColor; }
}
