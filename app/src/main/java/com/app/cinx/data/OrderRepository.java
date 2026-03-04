package com.app.cinx.data;

import com.app.cinx.model.Order;
import com.app.cinx.model.Order.Status;
import com.app.cinx.model.OrderItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * In-memory sample data for the Purchase History screen.
 *
 * Replace {@link #getOrders()} with a real network / database call when
 * a back-end is available; the rest of the screen code will not change
 * because it only depends on {@code List<Order>}.
 */
public class OrderRepository {

    // ─────────────────────────────────────────────────────────────────
    // Singleton
    // ─────────────────────────────────────────────────────────────────

    private static OrderRepository instance;

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────

    /** Returns all orders (newest first). */
    public List<Order> getOrders() {
        return SAMPLE_ORDERS;
    }

    /**
     * Returns orders filtered by status.
     * Pass {@code null} to get all orders.
     */
    public List<Order> getOrders(Status filter) {
        if (filter == null) return SAMPLE_ORDERS;
        List<Order> result = new java.util.ArrayList<>();
        for (Order o : SAMPLE_ORDERS) {
            if (o.getStatus() == filter) result.add(o);
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────────
    // Sample data (mirrors the HTML mock)
    // ─────────────────────────────────────────────────────────────────

    private static final List<Order> SAMPLE_ORDERS = Arrays.asList(

        new Order(
            "EDUF-8A9B2C",
            "12/03/2025",
            Status.COMPLETED,
            Collections.singletonList(new OrderItem(
                "UI/UX Design Masterclass: Từ Cơ Bản Đến Nâng Cao",
                "Nguyễn Thị Hà Linh",
                "https://images.unsplash.com/photo-1655720828018-edd2daec9349?w=200",
                1_200_000L,
                599_000L
            )),
            599_000L
        ),

        new Order(
            "EDUF-3X7Y9Z",
            "05/03/2025",
            Status.PENDING,
            Arrays.asList(
                new OrderItem(
                    "Fullstack React & Node.js cho người mới bắt đầu",
                    "Trần Minh Tuấn",
                    "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=200",
                    1_500_000L,
                    899_000L
                ),
                new OrderItem(
                    "React Native thực chiến",
                    "Trần Minh Tuấn",
                    "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=200",
                    800_000L,
                    499_000L
                ),
                new OrderItem(
                    "TypeScript từ A đến Z",
                    "Lê Văn Hùng",
                    "https://images.unsplash.com/photo-1726065235249-48f0a8b21c84?w=200",
                    600_000L,
                    299_000L
                )
            ),
            1_398_000L
        ),

        new Order(
            "EDUF-1K2L3M",
            "28/02/2025",
            Status.CANCELLED,
            Collections.singletonList(new OrderItem(
                "Digital Marketing 101: SEO & Ads",
                "Phạm Thanh Hương",
                "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=200",
                900_000L,
                450_000L
            )),
            450_000L
        ),

        new Order(
            "EDUF-7P8Q9R",
            "15/02/2025",
            Status.COMPLETED,
            Arrays.asList(
                new OrderItem(
                    "Python cho khoa học dữ liệu",
                    "Đặng Quốc Bảo",
                    "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=200",
                    1_100_000L,
                    550_000L
                ),
                new OrderItem(
                    "Machine Learning cơ bản với Scikit-learn",
                    "Đặng Quốc Bảo",
                    "https://images.unsplash.com/photo-1561736778-92e52a7769ef?w=200",
                    1_300_000L,
                    699_000L
                )
            ),
            1_249_000L
        ),

        new Order(
            "EDUF-2S3T4U",
            "01/02/2025",
            Status.PENDING,
            Collections.singletonList(new OrderItem(
                "Adobe Premiere Pro: Dựng phim chuyên nghiệp",
                "Hoàng Thị Mai",
                "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=200",
                850_000L,
                420_000L
            )),
            420_000L
        )
    );
}
