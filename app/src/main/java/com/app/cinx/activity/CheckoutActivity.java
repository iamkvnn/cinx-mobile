package com.app.cinx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CheckoutCourseAdapter;
import com.app.cinx.adapter.PaymentMethodAdapter;
import com.app.cinx.data.CartRepository;
import com.app.cinx.model.CartItem;
import com.app.cinx.model.PaymentMethod;
import com.app.cinx.utils.Convert;
import com.app.cinx.utils.UserManager;

import com.app.cinx.api.OrderService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CreateOrderRequest;
import com.app.cinx.api.dto.CartItemDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import com.app.cinx.utils.TokenManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checkout / Thanh-toán screen.
 *
 * Displays:
 *  1. Receiver information block
 *  2. Compact order-item list (read-only)
 *  3. Payment method selector (MoMo / VNPAY-QR / Thẻ Visa·Mastercard)
 *  4. Order-summary totals (subtotal + voucher discount)
 *
 * On "Đặt hàng":
 *  - Button shows a loading indicator for 1.5 s
 *  - Activity fades out and {@link PaymentSuccessActivity} is launched
 *
 * Architecture notes:
 *  - Cart items are read from {@link CartRepository} (singleton).
 *  - Applied-voucher info travels via Intent extras from CartActivity.
 *  - Payment logic is a stub; swap with real network calls later.
 */
public class CheckoutActivity extends AppCompatActivity {

    // ── Intent keys ──────────────────────────────────────────────────────
    public static final String EXTRA_VOUCHER_PERCENT = "extra_voucher_percent";
    public static final String EXTRA_VOUCHER_TITLE   = "extra_voucher_title";

    // ── Views ─────────────────────────────────────────────────────────────
    private TextView       tvOrderSectionTitle;
    private TextView       tvDetailSubtotalLabel;
    private TextView       tvDetailSubtotalValue;
    private TextView       tvDetailDiscountValue;
    private android.view.View layoutDetailDiscountRow;
    private TextView       tvCheckoutTotal;
    private AppCompatButton btnPlaceOrder;

    // ── Data ──────────────────────────────────────────────────────────────
    private List<CartItem> checkoutItems;  // items carried from cart

    /** Voucher applied in CartActivity (may be null). */
    private int    voucherPercent = 0;
    private String voucherTitle   = null;

    private PaymentMethodAdapter paymentAdapter;
    private boolean              isProcessing = false;

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        readExtras();
        collectCheckoutItems();
        bindViews();
        setupRecyclerViews();
        refreshSummary();
        setupClickListeners();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Initialisation helpers
    // ─────────────────────────────────────────────────────────────────────

    private void readExtras() {
        Intent intent = getIntent();
        voucherPercent = intent.getIntExtra(EXTRA_VOUCHER_PERCENT, 0);
        voucherTitle   = intent.getStringExtra(EXTRA_VOUCHER_TITLE);
    }

    /**
     * Grab only the *selected* items from the cart repository.
     * Falls back to all items if none are explicitly selected (e.g. direct launch).
     */
    private void collectCheckoutItems() {
        checkoutItems = new ArrayList<>();
        List<CartItem> all = CartRepository.getInstance().getItems();
        for (CartItem item : all) {
            if (item.isSelected()) checkoutItems.add(item);
        }
        if (checkoutItems.isEmpty()) {
            // Fallback: take everything in the cart
            checkoutItems.addAll(all);
        }
    }

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btnCheckoutBack);
        btnBack.setOnClickListener(v -> finish());

        tvOrderSectionTitle      = findViewById(R.id.tvOrderSectionTitle);
        tvDetailSubtotalLabel    = findViewById(R.id.tvDetailSubtotalLabel);
        tvDetailSubtotalValue    = findViewById(R.id.tvDetailSubtotalValue);
        layoutDetailDiscountRow  = findViewById(R.id.layoutDetailDiscountRow);
        tvDetailDiscountValue    = findViewById(R.id.tvDetailDiscountValue);
        tvCheckoutTotal          = findViewById(R.id.tvCheckoutTotal);
        btnPlaceOrder            = findViewById(R.id.btnPlaceOrder);

        // Receiver info — in a real app, pull from UserManager / profile API
        populateUserInfo();
    }

    private void populateUserInfo() {
        TextView tvName  = findViewById(R.id.tvReceiverName);
        TextView tvPhone = findViewById(R.id.tvReceiverPhone);
        TextView tvEmail = findViewById(R.id.tvReceiverEmail);

        UserManager user = UserManager.getInstance();
        if (user.isLoggedIn() && user.getUserEmail() != null) {
            tvEmail.setText("Email: " + user.getUserEmail());
        }
        // Name & phone are kept as sample values until a Profile model is added
    }

    // ─────────────────────────────────────────────────────────────────────
    // RecyclerViews
    // ─────────────────────────────────────────────────────────────────────

    private void setupRecyclerViews() {
        // Order items
        RecyclerView rvItems = findViewById(R.id.rvCheckoutItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new CheckoutCourseAdapter(checkoutItems));
        rvItems.setNestedScrollingEnabled(false);

        // Payment methods
        List<PaymentMethod> paymentMethods = buildPaymentMethods();
        paymentAdapter = new PaymentMethodAdapter(paymentMethods,
                method -> { /* selection handled inside adapter */ });

        RecyclerView rvPayment = findViewById(R.id.rvPaymentMethods);
        rvPayment.setLayoutManager(new LinearLayoutManager(this));
        rvPayment.setAdapter(paymentAdapter);
        rvPayment.setNestedScrollingEnabled(false);
    }

    /** Returns the static list of supported payment methods. */
    private List<PaymentMethod> buildPaymentMethods() {
        return Arrays.asList(
                new PaymentMethod(
                        PaymentMethod.Type.MOMO,
                        getString(R.string.pm_momo_name),
                        getString(R.string.pm_momo_desc),
                        R.drawable.ic_pm_momo,
                        R.drawable.bg_pm_momo),

                new PaymentMethod(
                        PaymentMethod.Type.VNPAY,
                        getString(R.string.pm_vnpay_name),
                        getString(R.string.pm_vnpay_desc),
                        R.drawable.ic_pm_vnpay,
                        R.drawable.bg_pm_vnpay),

                new PaymentMethod(
                        PaymentMethod.Type.CARD,
                        getString(R.string.pm_card_name),
                        getString(R.string.pm_card_desc),
                        R.drawable.ic_pm_card,
                        R.drawable.bg_pm_card)
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // Price summary  — single source of truth
    // ─────────────────────────────────────────────────────────────────────

    private long calcSubtotal() {
        long sum = 0;
        for (CartItem item : checkoutItems) sum += item.getSalePrice();
        return sum;
    }

    private long calcTotal() {
        long subtotal = calcSubtotal();
        if (voucherPercent <= 0) return subtotal;
        return subtotal - (subtotal * voucherPercent / 100);
    }

    private void refreshSummary() {
        int  count    = checkoutItems.size();
        long subtotal = calcSubtotal();
        long total    = calcTotal();
        long discount = subtotal - total;

        // Section title
        tvOrderSectionTitle.setText(getString(R.string.checkout_order_section, count));

        // Detail block
        tvDetailSubtotalLabel.setText(getString(R.string.checkout_subtotal, count));
        tvDetailSubtotalValue.setText(Convert.formatVnd(subtotal));

        if (voucherPercent > 0 && discount > 0) {
            layoutDetailDiscountRow.setVisibility(android.view.View.VISIBLE);
            tvDetailDiscountValue.setText("−" + Convert.formatVnd(discount));
        } else {
            layoutDetailDiscountRow.setVisibility(android.view.View.GONE);
        }

        // Bottom bar total
        tvCheckoutTotal.setText(Convert.formatVnd(total));
    }

    // ─────────────────────────────────────────────────────────────────────
    // Click listeners
    // ─────────────────────────────────────────────────────────────────────

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> {
            if (!isProcessing) startOrderProcessing();
        });
    }

    /**
     * Simulates a payment-processing round trip:
     *  1. Disable the button and show a "loading" label.
     *  2. After 1.5 s, launch {@link PaymentSuccessActivity} with order details.
     *  3. Fade out this screen to match the screenshot transition.
     */
    private void startOrderProcessing() {
        if (checkoutItems.isEmpty()) return;
        
        isProcessing = true;
        btnPlaceOrder.setText(getString(R.string.checkout_processing));
        btnPlaceOrder.setEnabled(false);

        // Dim the whole screen to give it a "processing" look
        android.view.View rootView = findViewById(android.R.id.content);
        rootView.animate()
                .alpha(0.6f)
                .setDuration(400)
                .start();

        OrderService orderService = RetrofitClient.getInstance().getOrderService();
        if (orderService == null) {
            new Handler(Looper.getMainLooper()).postDelayed(this::launchSuccessScreen, 1500);
            return;
        }

        CreateOrderRequest request = new CreateOrderRequest();
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        for (CartItem ci : checkoutItems) {
            CartItemDto dto = new CartItemDto();
            dto.setId(ci.getId());
            cartItemDtos.add(dto);
        }
        request.setCartItems(cartItemDtos);
        
        PaymentMethod pm = paymentAdapter.getSelectedMethod();
        request.setPaymentMethod(pm != null ? pm.getName() : "CARD");
        
        if (voucherTitle != null) {
            request.setVoucherCode(voucherTitle);
        }

        String token = TokenManager.getInstance().getBearerToken();
        orderService.createOrder(token, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    launchSuccessScreen();
                } else {
                    handleError();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Checkout", "Failed to place order", t);
                handleError();
            }
        });
    }
    
    private void handleError() {
        isProcessing = false;
        btnPlaceOrder.setText("Thử lại");
        btnPlaceOrder.setEnabled(true);
        android.view.View rootView = findViewById(android.R.id.content);
        rootView.animate().alpha(1.0f).setDuration(400).start();
        Toast.makeText(this, "Order failed, please try again", Toast.LENGTH_SHORT).show();
    }

    private void launchSuccessScreen() {
        // Generate a simple mock order code
        String orderCode = "EDUF-" + Integer.toHexString((int) (System.currentTimeMillis() % 0xFFFFF))
                .toUpperCase();

        UserManager user = UserManager.getInstance();
        String email = (user.isLoggedIn() && user.getUserEmail() != null)
                ? user.getUserEmail()
                : "minh.nguyen@example.com";

        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra(PaymentSuccessActivity.EXTRA_ORDER_CODE, orderCode);
        intent.putExtra(PaymentSuccessActivity.EXTRA_EMAIL, email);
        intent.putExtra(PaymentSuccessActivity.EXTRA_AMOUNT, Convert.formatVnd(calcTotal()));
        startActivity(intent);

        // Custom transition: fade out cart → success
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Clear the back stack up to (but not including) MainActivity
        CartRepository.getInstance().clear();
        finish();
    }
}
