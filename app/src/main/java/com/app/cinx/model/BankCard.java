package com.app.cinx.model;

import androidx.annotation.ColorInt;

/**
 * Represents a saved bank / credit / debit card in the user's payment methods.
 */
public class BankCard {

    public enum Network { VISA, MASTERCARD }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final String  id;
    private final Network network;
    private final String  cardholderName;
    private final String  lastFour;       // last 4 digits
    private final String  expiry;         // "MM/YY"
    @ColorInt private final int colorStart;
    @ColorInt private final int colorEnd;

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public BankCard(String id, Network network, String cardholderName,
                    String lastFour, String expiry,
                    @ColorInt int colorStart, @ColorInt int colorEnd) {
        this.id             = id;
        this.network        = network;
        this.cardholderName = cardholderName;
        this.lastFour       = lastFour;
        this.expiry         = expiry;
        this.colorStart     = colorStart;
        this.colorEnd       = colorEnd;
    }

    // ─────────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────────

    public String  getId()             { return id; }
    public Network getNetwork()        { return network; }
    public String  getCardholderName() { return cardholderName; }
    public String  getLastFour()       { return lastFour; }
    public String  getExpiry()         { return expiry; }
    @ColorInt public int getColorStart() { return colorStart; }
    @ColorInt public int getColorEnd()   { return colorEnd; }

    /** Masked card number string for display. */
    public String getMaskedNumber() {
        return "•••• •••• •••• " + lastFour;
    }
}
