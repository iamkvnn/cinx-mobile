package com.app.cinx;

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

import com.app.cinx.adapter.OrderAdapter;
import com.app.cinx.data.OrderRepository;
import com.app.cinx.model.Order;
import com.app.cinx.util.ToastUtil;

import java.util.List;

/**
 * PurchaseHistoryActivity
 * ───────────────────────
 * Displays the user's purchase history grouped into four filterable
 * status tabs: All · Completed · Pending · Cancelled.
 *
 * Architecture notes:
 *  - Data access is delegated to {@link OrderRepository}; swap the
 *    implementation for a ViewModel + LiveData when adding a real API.
 *  - The adapter receives interaction callbacks through
 *    {@link OrderAdapter.OnOrderActionListener} – no coupling between
 *    adapter and Activity beyond the interface.
 *  - Tab selection state is managed here (not in the adapter) to keep
 *    the adapter purely presentational.
 */
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

    private final OrderRepository repository = OrderRepository.getInstance();

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

        // Load all orders initially
        loadOrders(null);
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
        adapter = new OrderAdapter(repository.getOrders(null), this);
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
        List<Order> orders = repository.getOrders(filter);
        adapter.updateOrders(orders);

        boolean isEmpty = orders.isEmpty();
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
                break;
            case PENDING:
                msg = getString(R.string.order_cancel_toast, order.getOrderId());
                break;
            case CANCELLED:
            default:
                msg = getString(R.string.order_detail_toast, order.getOrderId());
                break;
        }
        ToastUtil.showCustomToast(this, msg);
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
                msg = getString(R.string.order_pay_toast, order.getOrderId());
                break;
            case CANCELLED:
            default:
                msg = getString(R.string.order_repurchase_toast);
                break;
        }
        ToastUtil.showCustomToast(this, msg);
    }
}
