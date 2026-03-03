package com.app.cinx.data;

import com.app.cinx.model.CartItem;

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

    private final List<CartItem> items = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────

    /** Live list — may be mutated by callers directly or via helpers below. */
    public List<CartItem> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean containsCourse(int courseId) {
        for (CartItem item : items) {
            if (item.getId() == courseId) return true;
        }
        return false;
    }

    public void addItem(CartItem item) {
        if (!containsCourse(item.getId())) {
            items.add(item);
        }
    }

    public void removeItem(int courseId) {
        items.removeIf(item -> item.getId() == courseId);
    }

    public void clear() {
        items.clear();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Sample data  — identical to what CartActivity previously inlined
    // ─────────────────────────────────────────────────────────────────────

    private void seedSampleData() {
        items.addAll(Arrays.asList(
                new CartItem(1,
                        "UI/UX Design Masterclass: Từ Cơ Bản Đến Nâng Cao",
                        "Hà Linh",
                        1_200_000L, 599_000L,
                        "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400",
                        "Design"),
                new CartItem(2,
                        "Fullstack React & Node.js cho người mới",
                        "Minh Tuấn",
                        1_500_000L, 899_000L,
                        "https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=400",
                        "Code")
        ));
    }
}
