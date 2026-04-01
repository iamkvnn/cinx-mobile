package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.OrderAdapter;
import com.app.cinx.model.Order;
import com.app.cinx.model.OrderItem;
import com.app.cinx.api.OrderService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiListResponse;
import com.app.cinx.api.dto.OrderDto;
import com.app.cinx.api.dto.OrderItemDto;
import com.app.cinx.utils.TokenManager;
import com.app.cinx.utils.ToastUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity
        implements OrderAdapter.OnOrderActionListener {

    // ─────────────────────────────────────────────────────────────────
    // View references
    // ─────────────────────────────────────────────────────────────────

    private ImageButton  btnBack;
    private ImageButton  btnSearch;

    // Tabs
    private HorizontalScrollView hsvTabs;
    private TextView tabAll;
    private TextView tabCompleted;
    private TextView tabPending;
    private TextView tabCancelled;

    // List
    private RecyclerView     rvOrders;
    private OrderAdapter     adapter;

    // Empty state
    private LinearLayout layoutOrderEmpty;
    private Button       btnEmptyBrowse;

    // ─────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────

    private final List<Order> allOrders = new ArrayList<>();

    /** Currently selected filter; null = "All". */
    private Order.Status activeFilter = null;

    // ─────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        bindViews();
        setupRecyclerView();
        setupTabListeners();
        setupActionBarButtons();

        fetchOrders();
    }

    private void fetchOrders() {
        OrderService service = RetrofitClient.getInstance().getOrderService();
        service.getOrders().enqueue(new Callback<ApiListResponse<OrderDto>>() {
            @Override
            public void onResponse(Call<ApiListResponse<OrderDto>> call, Response<ApiListResponse<OrderDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    allOrders.clear();
                    for (OrderDto dto : response.body().getData()) {
                        Order.Status status = Order.Status.PENDING;
                        if (dto.getStatus() != null) {
                            try {
                                status = Order.Status.valueOf(dto.getStatus().toUpperCase());
                            } catch (Exception e) {}
                        }
                        
                        List<OrderItem> items = new ArrayList<>();
                        if (dto.getItems() != null) {
                            for (OrderItemDto itemDto : dto.getItems()) {
                                items.add(new OrderItem(
                                        itemDto.getTitle(), 
                                        "Instructor", // Fallback if no instructor in dto
                                        "https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=300", // Fallback image
                                        itemDto.getPrice(), 
                                        itemDto.getDiscountedPrice()
                                ));
                            }
                        }
                        
                        long totalAmount = dto.getTotalPrice(); // Get total price from DTO
                        allOrders.add(new Order(dto.getId(), dto.getOrderDate(), status, items, totalAmount));
                    }
                    runOnUiThread(() -> loadOrders(activeFilter));
                }
            }

            @Override
            public void onFailure(Call<ApiListResponse<OrderDto>> call, Throwable t) {
                ToastUtil.showCustomToast(PurchaseHistoryActivity.this, "Failed to load orders");
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        btnBack    = findViewById(R.id.btnBack);
        btnSearch  = findViewById(R.id.btnSearch);

        hsvTabs      = findViewById(R.id.hsvTabs);

        tabAll       = findViewById(R.id.tabAll);
        tabCompleted = findViewById(R.id.tabCompleted);
        tabPending   = findViewById(R.id.tabPending);
        tabCancelled = findViewById(R.id.tabCancelled);

        rvOrders          = findViewById(R.id.rvOrders);
        layoutOrderEmpty  = findViewById(R.id.layoutOrderEmpty);
        btnEmptyBrowse    = findViewById(R.id.btnOrderEmptyBrowse);
    }

    // ─────────────────────────────────────────────────────────────────
    // RecyclerView setup
    // ─────────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new OrderAdapter(new ArrayList<>(), this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
        rvOrders.setHasFixedSize(false);
    }

    // ─────────────────────────────────────────────────────────────────
    // Tab listeners
    // ─────────────────────────────────────────────────────────────────

    private void setupTabListeners() {
        tabAll.setOnClickListener(v       -> selectTab(null));
        tabCompleted.setOnClickListener(v -> selectTab(Order.Status.COMPLETED));
        tabPending.setOnClickListener(v   -> selectTab(Order.Status.PENDING));
        tabCancelled.setOnClickListener(v -> selectTab(Order.Status.CANCELLED));
    }

    /**
     * Updates tab styling and reloads the order list for the given filter.
     *
     * @param status {@code null} selects the "All" tab.
     */
    private void selectTab(Order.Status status) {
        if (activeFilter == status) return; // no-op if already selected
        activeFilter = status;

        // Update tab appearance
        updateTabStyle(tabAll,       status == null);
        updateTabStyle(tabCompleted, status == Order.Status.COMPLETED);
        updateTabStyle(tabPending,   status == Order.Status.PENDING);
        updateTabStyle(tabCancelled, status == Order.Status.CANCELLED);

        // Scroll the selected tab into view inside the HorizontalScrollView
        final TextView activeTab;
        if (status == null)                        activeTab = tabAll;
        else if (status == Order.Status.COMPLETED) activeTab = tabCompleted;
        else if (status == Order.Status.PENDING)   activeTab = tabPending;
        else                                       activeTab = tabCancelled;

        hsvTabs.post(() -> {
            int scrollX = activeTab.getLeft()
                    - (hsvTabs.getWidth() / 2)
                    + (activeTab.getWidth() / 2);
            hsvTabs.smoothScrollTo(Math.max(0, scrollX), 0);
        });

        loadOrders(status);
    }

    private void updateTabStyle(TextView tab, boolean isActive) {
        if (isActive) {
            tab.setBackgroundResource(R.drawable.bg_order_tab_active);
            tab.setTextColor(getColor(R.color.order_tab_active_text));
        } else {
            tab.setBackgroundResource(R.drawable.bg_order_tab_inactive);
            tab.setTextColor(getColor(R.color.order_tab_inactive_text));
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Data loading
    // ─────────────────────────────────────────────────────────────────

    private void loadOrders(Order.Status filter) {
        List<Order> filtered = new ArrayList<>();
        if (filter == null) {
            filtered.addAll(allOrders);
        } else {
            for (Order o : allOrders) {
                if (o.getStatus() == filter) {
                    filtered.add(o);
                }
            }
        }
        
        adapter.updateOrders(filtered);

        boolean isEmpty = filtered.isEmpty();
        rvOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutOrderEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    // ─────────────────────────────────────────────────────────────────
    // Action bar buttons
    // ─────────────────────────────────────────────────────────────────

    private void setupActionBarButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v ->
                ToastUtil.showCustomToast(this,
                        getString(R.string.profile_coming_soon)));

        btnEmptyBrowse.setOnClickListener(v -> {
            Intent intent = new Intent(this, DiscoveryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    // ─────────────────────────────────────────────────────────────────
    // OrderAdapter.OnOrderActionListener
    // ─────────────────────────────────────────────────────────────────

    @Override
    public void onLeftAction(Order order) {
        String msg;
        switch (order.getStatus()) {
            case COMPLETED:
                msg = getString(R.string.order_review_toast, order.getOrderId());
                ToastUtil.showCustomToast(this, msg);
                break;
            case PENDING:
                msg = getString(R.string.order_cancel_toast, order.getOrderId());
                ToastUtil.showCustomToast(this, msg);
                break;
            case CANCELLED:
            default:
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRightAction(Order order) {
        String msg;
        switch (order.getStatus()) {
            case COMPLETED:
                // Navigate to learning screen
                Intent intent = new Intent(this, MyLearningActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return;

            case PENDING:
                Intent pendingIntent = new Intent(this, OrderDetailActivity.class);
                pendingIntent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
                startActivity(pendingIntent);
                break;
            case CANCELLED:
            default:
                msg = getString(R.string.order_repurchase_toast);
                ToastUtil.showCustomToast(this, msg);
                break;
        }
    }
}
