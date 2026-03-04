package com.app.cinx.model;

import java.util.List;

/**
 * Represents a purchase order.
 *
 * Status values are encoded as the {@link Status} enum so comparisons
 * are exhaustive and compile-time safe. The tab filter string is
 * deliberately kept as a separate concept to avoid coupling UI labels
 * to the model.
 */
public class Order {

    // ─────────────────────────────────────────────────────────────────
    // Status
    // ─────────────────────────────────────────────────────────────────

    public enum Status {
        COMPLETED,   // Hoàn thành
        PENDING,     // Chờ thanh toán
        CANCELLED    // Đã hủy
    }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final String          orderId;
    private final String          date;       // display string, e.g. "12/03/2025"
    private final Status          status;
    private final List<OrderItem> items;
    private final long            totalAmount; // VND

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public Order(String orderId, String date, Status status,
                 List<OrderItem> items, long totalAmount) {
        this.orderId     = orderId;
        this.date        = date;
        this.status      = status;
        this.items       = items;
        this.totalAmount = totalAmount;
    }

    // ─────────────────────────────────────────────────────────────────
    // Accessors
    // ─────────────────────────────────────────────────────────────────

    public String          getOrderId()     { return orderId; }
    public String          getDate()        { return date; }
    public Status          getStatus()      { return status; }
    public List<OrderItem> getItems()       { return items; }
    public long            getTotalAmount() { return totalAmount; }
}
