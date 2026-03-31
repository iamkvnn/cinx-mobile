package com.app.cinx.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cinx.R;
import com.app.cinx.adapter.CheckoutCourseAdapter;
import com.app.cinx.api.dto.CartItemResponse;
import com.app.cinx.api.dto.CourseResponse;
import com.app.cinx.api.dto.InstructorResponse;
import com.app.cinx.api.EnrollmentService;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.OrderDetailResponse;
import com.app.cinx.api.dto.OrderItemResponse;
import com.app.cinx.utils.Convert;
import com.app.cinx.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    
    private ImageButton btnBack;
    
    private LinearLayout statusBanner;
    private ImageView ivStatusIcon;
    private TextView tvStatusTitle;
    private TextView tvStatusSubtitle;
    
    private RecyclerView rvOrderItems;
    private CheckoutCourseAdapter adapter;
    
    private TextView tvSubtotal, tvDiscount, tvTotal;
    private TextView tvPaymentMethod;
    private TextView tvOrderId, tvOrderDate;
    private ImageButton btnCopyId;
    
    private View bottomAction;
    private View btnLearnNow;
    
    // Default mock data, replace with repository lookup
    private String orderId = "";
    private OrderDetailResponse orderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        setupListeners();
        
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ORDER_ID)) {
            orderId = intent.getStringExtra(EXTRA_ORDER_ID);
            fetchOrderDetail();
        } else {
            ToastUtil.showCustomToast(this, "Order ID missing");
            finish();
        }
    }

    private void fetchOrderDetail() {
        EnrollmentService service = RetrofitClient.getInstance().getEnrollmentService();
        if (service == null) return;

        service.getOrderById(orderId).enqueue(new Callback<ApiResponse<OrderDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetailResponse>> call, Response<ApiResponse<OrderDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    orderDetail = response.body().getData();
                    updateUI();
                } else {
                    ToastUtil.showCustomToast(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetailResponse>> call, Throwable t) {
                ToastUtil.showCustomToast(OrderDetailActivity.this, "Lỗi kết nối");
            }
        });
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        
        statusBanner = findViewById(R.id.statusBanner);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusSubtitle = findViewById(R.id.tvStatusSubtitle);
        
        rvOrderItems = findViewById(R.id.rvOrderItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        // Use checkout course adapter to show courses in order
        adapter = new CheckoutCourseAdapter(new ArrayList<>());
        rvOrderItems.setAdapter(adapter);
        
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        btnCopyId = findViewById(R.id.btnCopyId);
        
        bottomAction = findViewById(R.id.bottomAction);
        btnLearnNow = findViewById(R.id.btnLearnNow);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnCopyId.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order ID", tvOrderId.getText());
            clipboard.setPrimaryClip(clip);
            ToastUtil.showCustomToast(this, "Đã chép mã đơn hàng");
        });
        
        btnLearnNow.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });
    }

    private void updateUI() {
        if (orderDetail == null) return;
        
        tvOrderId.setText(orderId);
        tvOrderDate.setText(orderDetail.getOrderDate() != null ? orderDetail.getOrderDate() : "");

        String paymentStatus = "";
        if (orderDetail.getPayment() != null && orderDetail.getPayment().getStatus() != null) {
            paymentStatus = orderDetail.getPayment().getStatus().toUpperCase();
        }
        
        // Apply status differences
        if ("SUCCESS".equals(paymentStatus) || "COMPLETED".equals(paymentStatus)) { // Success
            statusBanner.setBackgroundResource(R.drawable.bg_btn_primary); // Assuming gradient for success
            ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
            tvStatusTitle.setText("Giao dịch thành công");
            tvStatusSubtitle.setText("Cảm ơn bạn đã mua khóa học.");
            bottomAction.setVisibility(View.VISIBLE);
        } else if ("PENDING".equals(paymentStatus) || "".equals(paymentStatus)) { // Unpaid
            statusBanner.setBackgroundColor(Color.parseColor("#FBBF24")); // Amber
            ivStatusIcon.setImageResource(R.drawable.ic_clock); // Use appropriate icon
            tvStatusTitle.setText("Chờ thanh toán");
            tvStatusSubtitle.setText("Vui lòng hoàn tất thanh toán của bạn.");
            bottomAction.setVisibility(View.GONE);
        } else { // Cancelled
            statusBanner.setBackgroundColor(Color.parseColor("#EF4444")); // Red
            ivStatusIcon.setImageResource(R.drawable.ic_close);
            tvStatusTitle.setText("Đã hủy hoặc thất bại");
            tvStatusSubtitle.setText("Giao dịch này không thành công.");
            bottomAction.setVisibility(View.GONE);
        }
        
        List<CartItemResponse> displayItems = new ArrayList<>();
        if (orderDetail.getItems() != null) {
            for (OrderItemResponse item : orderDetail.getItems()) {
                CartItemResponse c = new CartItemResponse();
                c.setId(item.getCourseId());
                
                CourseResponse cr = new CourseResponse();
                cr.setId(item.getCourseId());
                cr.setTitle(item.getTitle());
                cr.setPrice(item.getPrice());
                cr.setDiscountedPrice(item.getDiscountedPrice());
                
                InstructorResponse ir = new InstructorResponse();
                ir.setName("Instructor");
                cr.setInstructor(ir);
                
                c.setCourse(cr);
                displayItems.add(c);
            }
        }
        
        // Re-create the adapter because setCourses isn't available
        adapter = new CheckoutCourseAdapter(displayItems);
        rvOrderItems.setAdapter(adapter);

        long subtotal = 0;
        if (orderDetail.getItems() != null) {
             for (OrderItemResponse item : orderDetail.getItems()) {
                 subtotal += item.getPrice() != null ? item.getPrice() : 0;
             }
        }

        long discount = orderDetail.getDiscounted() != null ? orderDetail.getDiscounted() : 0;
        long total = orderDetail.getTotalPrice() != null ? orderDetail.getTotalPrice() : (subtotal - discount);

        tvSubtotal.setText(Convert.formatVnd(subtotal));
        tvDiscount.setText("-" + Convert.formatVnd(discount));
        tvTotal.setText(Convert.formatVnd(total));
        
        if (orderDetail.getPayment() != null && orderDetail.getPayment().getPaymentInfo() != null) {
            tvPaymentMethod.setText(orderDetail.getPayment().getPaymentInfo());
        } else {
            tvPaymentMethod.setText("N/A");
        }
    }
}
