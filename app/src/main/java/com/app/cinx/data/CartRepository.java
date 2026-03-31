package com.app.cinx.data;

import com.app.cinx.api.dto.CartItemResponse;
import com.app.cinx.api.dto.CourseResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton that serves as the single source of truth for cart state
 * across the whole app (CartActivity badge updates, detail screens, etc.).
 *
 * In a real project this would be backed by Room + ViewModel; here it is
 * kept deliberately simple so any Activity can read/write it without DI.
 */
public class CartRepository {

    // ─────────────────────────────────────────────────────────────────────
    // Singleton
    // ─────────────────────────────────────────────────────────────────────

    private static CartRepository instance;

    private CartRepository() {
        seedSampleData();
    }

    public static CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────
    // State
    // ─────────────────────────────────────────────────────────────────────

    private final List<CartItemResponse> items = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────

    /** Live list — may be mutated by callers directly or via helpers below. */
    public List<CartItemResponse> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean containsCourse(String courseId) {
        for (CartItemResponse item : items) {
            String itemId = item.getCourse() != null ? item.getCourse().getId() : item.getId();
            if (itemId != null && itemId.equals(courseId)) return true;
        }
        return false;
    }

    public void addItem(CartItemResponse item) {
        String itemId = item.getCourse() != null ? item.getCourse().getId() : item.getId();
        if (itemId != null && !containsCourse(itemId)) {
            items.add(item);
        }
    }

    public void removeItem(String courseId) {
        items.removeIf(item -> {
            String itemId = item.getCourse() != null ? item.getCourse().getId() : item.getId();
            return itemId != null && itemId.equals(courseId);
        });
    }

    public void clear() {
        items.clear();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Sample data  — identical to what CartActivity previously inlined
    // ─────────────────────────────────────────────────────────────────────

    private void seedSampleData() {
        // No sample data seeded initially
    }
}
