package com.app.cinx.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CartAdapter;
import com.app.cinx.adapter.VoucherAdapter;
import com.app.cinx.data.CartRepository;
import com.app.cinx.model.CartItem;
import com.app.cinx.model.Voucher;
import com.app.cinx.utils.Convert;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.app.cinx.api.CartService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CartItemResponse;
import com.app.cinx.api.dto.CourseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cart screen — lets the user review selected courses, apply vouchers,
 * and proceed to checkout.
 *
 * Architecture notes:
 *  - Sample data is defined inline; swap with a ViewModel/Repository later.
 *  - All price math is kept in {@link #refreshSummary()} — one source of truth.
 *  - Swipe-to-delete uses {@link ItemTouchHelper} with a custom paint callback
 *    so no extra library dependency is required.
 */
public class CartActivity extends AppCompatActivity
        implements CartAdapter.OnCartInteractionListener {

    // ─────────────────────────────────────────────────────────────────────
    // Views
    // ─────────────────────────────────────────────────────────────────────

    private CheckBox         cbSelectAll;
    private RecyclerView     rvCart;
    private TextView         tvVoucherLabel;
    private TextView         tvVoucherBadge;
    private TextView         tvSubtotalLabel;
    private TextView         tvSubtotalValue;
    private View             layoutDiscountRow;
    private TextView         tvDiscountValue;
    private TextView         tvTotalValue;
    private TextView         tvBottomTotal;
    private AppCompatButton  btnCheckout;
    private LinearLayout     layoutCartContent;
    private LinearLayout     layoutEmptyState;
    private AppCompatButton  btnExplore;
    private View             layoutVoucherRow;
    private View             cvPaymentSummary;

    // ─────────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────────

    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<Voucher>  vouchers  = new ArrayList<>();
    private       Voucher        appliedVoucher = null;
    private       CartAdapter    cartAdapter;

    // ─────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initSampleData();
        bindViews();
        setupRecyclerView();
        setupSwipeToDelete();
        setupClickListeners();
        
        fetchCartItems();
    }
    
    private void fetchCartItems() {
        CartService cartService = RetrofitClient.getInstance().getCartService();
        if (cartService == null) return;
        
        cartService.getCart().enqueue(new Callback<ApiResponse<List<CartItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CartItemResponse>>> call, Response<ApiResponse<List<CartItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    cartItems.clear();
                    CartRepository.getInstance().clear();
                    
                    List<CartItemResponse> responses = response.body().getData();
                    for (CartItemResponse r : responses) {
                        CourseResponse cr = r.getCourse();
                        if (cr == null) continue;
                        
                        String id = r.getId() != null ? r.getId() : cr.getId();
                        String title = cr.getTitle();
                        String instructor = cr.getDescription(); // fallback
                        long price = cr.getPrice() != null ? cr.getPrice() : 0L;
                        long discountedPrice = cr.getDiscountedPrice() != null ? cr.getDiscountedPrice() : price;
                        String thumbnail = "https://images.unsplash.com/photo-1586717791821-3f44a5638d48?w=300&q=80";
                        String category = cr.getCategory();
                        
                        CartItem ci = new CartItem(id, title, instructor, price, discountedPrice, thumbnail, category);
                        ci.setSelected(true); // default selected
                        cartItems.add(ci);
                        CartRepository.getInstance().addItem(ci);
                    }
                    runOnUiThread(() -> {
                        cartAdapter.notifyDataSetChanged();
                        refreshSummary();
                        updateEmptyState();
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CartItemResponse>>> call, Throwable t) {
                Log.e("CartActivity", "Failed to fetch cart", t);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────
    // Sample data — replace with Repository in production
    // ─────────────────────────────────────────────────────────────────────

    private void initSampleData() {
        // Vouchers mock
        vouchers.addAll(Arrays.asList(
                new Voucher("v1", "Giảm 20% cho thành viên mới",
                        "Áp dụng cho mọi khóa học", "EDUFUTURE", 20),
                new Voucher("v2", "Giảm 10% Lập trình",
                        "Dành riêng cho khóa Code",  "PRODEV",    10),
                new Voucher("v3", "Giảm 5% Thiết kế",
                        "Dành riêng cho khóa Design","DESIGN50",  5)
        ));
    }

    // ─────────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────────

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btnCartBack);
        TextView    btnClearAll = findViewById(R.id.btnClearAll);

        cbSelectAll       = findViewById(R.id.cbSelectAll);
        rvCart            = findViewById(R.id.rvCart);
        tvVoucherLabel    = findViewById(R.id.tvVoucherLabel);
        tvVoucherBadge    = findViewById(R.id.tvVoucherBadge);
        tvSubtotalLabel   = findViewById(R.id.tvSubtotalLabel);
        tvSubtotalValue   = findViewById(R.id.tvSubtotalValue);
        layoutDiscountRow = findViewById(R.id.layoutDiscountRow);
        tvDiscountValue   = findViewById(R.id.tvDiscountValue);
        tvTotalValue      = findViewById(R.id.tvTotalValue);
        tvBottomTotal     = findViewById(R.id.tvBottomTotal);
        btnCheckout       = findViewById(R.id.btnCheckout);
        layoutCartContent = findViewById(R.id.layoutCartContent);
        layoutEmptyState  = findViewById(R.id.layoutEmptyState);
        btnExplore        = findViewById(R.id.btnExplore);
        layoutVoucherRow  = findViewById(R.id.layoutVoucherRow);
        cvPaymentSummary  = findViewById(R.id.cvPaymentSummary);

        btnBack.setOnClickListener(v -> finish());
        btnClearAll.setOnClickListener(v -> clearAll());
    }

    // ─────────────────────────────────────────────────────────────────────
    // RecyclerView
    // ─────────────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);
        rvCart.setHasFixedSize(false);
        // Disable over-scroll so it scrolls in the parent NestedScrollView cleanly
        rvCart.setNestedScrollingEnabled(false);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Swipe-to-delete via ItemTouchHelper
    // ─────────────────────────────────────────────────────────────────────

    private void setupSwipeToDelete() {
        int deleteColor = ContextCompat.getColor(this, R.color.cart_delete_bg);
        Drawable trashIcon = ContextCompat.getDrawable(this, R.drawable.ic_close);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                CartItem item = cartItems.get(pos);
                
                // Call API to remove
                CartService cartService = RetrofitClient.getInstance().getCartService();
                if (cartService != null) {
                    cartService.removeFromCart(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                // optional handling
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Log.e("CartActivity", "Failed to remove item", t);
                        }
                    });
                }
                
                cartAdapter.removeItem(pos);
                updateEmptyState();
                // refreshSummary is called by cartAdapter via callback
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float cornerRadius = Convert.dpToPx(CartActivity.this, 16);

                    // Draw red background
                    Paint bgPaint = new Paint();
                    bgPaint.setColor(deleteColor);
                    RectF bg = new RectF(
                            itemView.getRight() + dX,
                            itemView.getTop()   + Convert.dpToPx(CartActivity.this, 4),
                            itemView.getRight(),
                            itemView.getBottom()- Convert.dpToPx(CartActivity.this, 4)
                    );
                    c.drawRoundRect(bg, cornerRadius, cornerRadius, bgPaint);

                    // Draw trash icon
                    if (trashIcon != null) {
                        int iconSize  = Convert.dpToPx(CartActivity.this, 24);
                        int iconMargin= Convert.dpToPx(CartActivity.this, 20);
                        int iconTop   = itemView.getTop()   + (itemView.getHeight() - iconSize) / 2;
                        int iconLeft  = itemView.getRight() - iconMargin - iconSize;
                        trashIcon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
                        trashIcon.setTint(Color.WHITE);
                        trashIcon.draw(c);
                    }

                    // Fade the item as it's swiped
                    float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(Math.max(alpha, 0.3f));
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh) {
                super.clearView(rv, vh);
                vh.itemView.setAlpha(1.0f);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.4f;
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(rvCart);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Click listeners
    // ─────────────────────────────────────────────────────────────────────

    private void setupClickListeners() {
        // Select-all checkbox
        cbSelectAll.setOnCheckedChangeListener((btn, checked) -> {
            cartAdapter.setAllSelected(checked);
        });

        // Voucher row → open bottom sheet
        layoutVoucherRow.setOnClickListener(v -> showVoucherBottomSheet());

        // Explore button on empty state
        btnExplore.setOnClickListener(v -> finish()); // navigate back / to Discovery

        // Checkout
        btnCheckout.setOnClickListener(v -> {
            long total = calculateTotal();
            if (total == 0) {
                Toast.makeText(this, getString(R.string.cart_no_items_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CheckoutActivity.class);
            if (appliedVoucher != null) {
                intent.putExtra(CheckoutActivity.EXTRA_VOUCHER_PERCENT,
                        appliedVoucher.getDiscountPercent());
                intent.putExtra(CheckoutActivity.EXTRA_VOUCHER_TITLE,
                        appliedVoucher.getTitle());
            }
            startActivity(intent);
        });
    }

    // ─────────────────────────────────────────────────────────────────────
    // CartAdapter.OnCartInteractionListener
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void onSelectionChanged() {
        refreshSummary();
        // Sync cart repository so CheckoutActivity receives up-to-date selection state
        CartRepository.getInstance().clear();
        for (CartItem item: cartItems) {
            CartRepository.getInstance().addItem(item);
        }

        // Sync "select all" checkbox without triggering its own listener
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(cartAdapter.areAllSelected());
        cbSelectAll.setOnCheckedChangeListener((btn, checked) ->
                cartAdapter.setAllSelected(checked));
    }

    @Override
    public void onDeleteItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            CartItem item = cartItems.get(position);
            CartService cartService = RetrofitClient.getInstance().getCartService();
            if (cartService != null) {
                cartService.removeFromCart(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
                });
            }
        }
        cartAdapter.removeItem(position);
        updateEmptyState();
        refreshSummary();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Price calculation  — single source of truth
    // ─────────────────────────────────────────────────────────────────────

    private long calculateSubtotal() {
        long subtotal = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) subtotal += item.getSalePrice();
        }
        return subtotal;
    }

    private long calculateTotal() {
        long subtotal = calculateSubtotal();
        if (appliedVoucher == null || subtotal == 0) return subtotal;
        long discount = subtotal * appliedVoucher.getDiscountPercent() / 100;
        return subtotal - discount;
    }

    private void refreshSummary() {
        long subtotal   = calculateSubtotal();
        long total      = calculateTotal();
        long discount   = subtotal - total;

        int count = 0;
        for (CartItem item : cartItems) if (item.isSelected()) count++;

        // Only show voucher row & payment summary if at least 1 item is selected
        if (count > 0) {
            layoutVoucherRow.setVisibility(View.VISIBLE);
            cvPaymentSummary.setVisibility(View.VISIBLE);
        } else {
            layoutVoucherRow.setVisibility(View.GONE);
            cvPaymentSummary.setVisibility(View.GONE);
            appliedVoucher = null; // Clear voucher if nothing is selected
            total = 0; // Ensure total is 0
        }

        // Subtotal label
        tvSubtotalLabel.setText(getString(R.string.cart_subtotal_label, count));
        tvSubtotalValue.setText(Convert.formatVnd(subtotal));

        // Discount row — only show when a voucher is applied AND items are selected
        if (appliedVoucher != null && discount > 0) {
            layoutDiscountRow.setVisibility(View.VISIBLE);
            tvDiscountValue.setText("- " + Convert.formatVnd(discount));
        } else {
            layoutDiscountRow.setVisibility(View.GONE);
        }

        // Total
        tvTotalValue.setText(Convert.formatVnd(total));
        tvBottomTotal.setText(Convert.formatVnd(total));

        // Voucher label row
        if (appliedVoucher != null) {
            tvVoucherLabel.setText(appliedVoucher.getTitle());
            tvVoucherBadge.setVisibility(View.VISIBLE);
            tvVoucherBadge.setText("−" + appliedVoucher.getDiscountPercent() + "%");
        } else {
            tvVoucherLabel.setText(getString(R.string.cart_voucher_hint));
            tvVoucherBadge.setVisibility(View.GONE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Clear all
    // ─────────────────────────────────────────────────────────────────────

    private void clearAll() {
        CartService cartService = RetrofitClient.getInstance().getCartService();
        if (cartService != null) {
            cartService.clearCart().enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}
                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        }
        
        int size = cartItems.size();
        cartItems.clear();
        cartAdapter.notifyItemRangeRemoved(0, size);
        appliedVoucher = null;
        refreshSummary();
        updateEmptyState();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Empty state
    // ─────────────────────────────────────────────────────────────────────

    private void updateEmptyState() {
        boolean isEmpty = cartItems.isEmpty();
        layoutCartContent.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        findViewById(R.id.bottomActionBar).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (isEmpty) {
            cbSelectAll.setVisibility(View.GONE);
        } else {
            cbSelectAll.setVisibility(View.VISIBLE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Voucher Bottom Sheet
    // ─────────────────────────────────────────────────────────────────────

    private void showVoucherBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this)
                .inflate(R.layout.layout_voucher_bottom_sheet, null);
        dialog.setContentView(sheetView);

        // Promo code input
        EditText    etPromo   = sheetView.findViewById(R.id.etPromoCode);
        AppCompatButton btnApply = sheetView.findViewById(R.id.btnApplyPromo);

        // Voucher RecyclerView
        RecyclerView rvVouchers = sheetView.findViewById(R.id.rvVouchers);
        AppCompatButton btnConfirmVoucher = sheetView.findViewById(R.id.btnConfirmVoucher);
        String currentId = appliedVoucher != null ? appliedVoucher.getId() : null;

        VoucherAdapter voucherAdapter = new VoucherAdapter(vouchers, currentId, selected -> {
            // Just select, don't dismiss
        });

        rvVouchers.setLayoutManager(new LinearLayoutManager(this));
        rvVouchers.setAdapter(voucherAdapter);

        // Confirm button click
        btnConfirmVoucher.setOnClickListener(v -> {
            appliedVoucher = voucherAdapter.getSelectedVoucher();
            refreshSummary();
            dialog.dismiss();
        });

        // Apply typed promo code
        btnApply.setOnClickListener(v -> {
            String code = etPromo.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, getString(R.string.cart_enter_promo_code), Toast.LENGTH_SHORT).show();
                return;
            }
            Voucher matched = null;
            for (Voucher voucher : vouchers) {
                if (voucher.getCode().equalsIgnoreCase(code)) {
                    matched = voucher;
                    break;
                }
            }
            if (matched != null) {
                appliedVoucher = matched;
                refreshSummary();
                dialog.dismiss();
                Toast.makeText(this, getString(R.string.cart_promo_applied, matched.getDiscountPercent()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.cart_promo_invalid), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet)
                    .setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
