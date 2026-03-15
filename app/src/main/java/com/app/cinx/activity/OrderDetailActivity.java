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
import com.app.cinx.model.Order;
import com.app.cinx.util.ToastUtil;

import java.util.ArrayList;

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
    private String orderId = "EDUF-8A9B2C";
    private int status = 1; // 0=Unpaid, 1=Success, 2=Cancelled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        setupListeners();
        
        // Mock data logic
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ORDER_ID)) {
            orderId = intent.getStringExtra(EXTRA_ORDER_ID);
        }
        
        // Populate UI
        updateUI();
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
        tvOrderId.setText(orderId);
        tvOrderDate.setText("24/05/2026 14:28");
        
        // Apply status differences
        if (status == 1) { // Success
            statusBanner.setBackgroundResource(R.drawable.bg_btn_primary); // Assuming gradient for success
            ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
            tvStatusTitle.setText("Giao dịch thành công");
            tvStatusSubtitle.setText("Cảm ơn bạn đã mua khóa học.");
            bottomAction.setVisibility(View.VISIBLE);
        } else if (status == 0) { // Unpaid
            statusBanner.setBackgroundColor(Color.parseColor("#FBBF24")); // Amber
            ivStatusIcon.setImageResource(R.drawable.ic_clock); // Use appropriate icon
            tvStatusTitle.setText("Chờ thanh toán");
            tvStatusSubtitle.setText("Vui lòng hoàn tất thanh toán của bạn.");
            bottomAction.setVisibility(View.GONE);
        } else { // Cancelled
            statusBanner.setBackgroundColor(Color.parseColor("#EF4444")); // Red
            ivStatusIcon.setImageResource(R.drawable.ic_close);
            tvStatusTitle.setText("Đã hủy");
            tvStatusSubtitle.setText("Giao dịch này đã bị hủy.");
            bottomAction.setVisibility(View.GONE);
        }
        
        // Mock payment details
        tvSubtotal.setText("1.498.000đ");
        tvDiscount.setText("-299.600đ");
        tvTotal.setText("1.198.400đ");
        tvPaymentMethod.setText("Ví MoMo");
    }
}
