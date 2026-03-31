package com.app.cinx.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.Activity;

import com.app.cinx.R;
import com.app.cinx.api.RetrofitClient;
import com.app.cinx.api.UserService;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.UpdateProfileRequest;
import com.app.cinx.api.dto.UserDto;
import com.app.cinx.utils.NavHelper;
import com.app.cinx.utils.ToastUtil;
import com.app.cinx.utils.UserManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

/**
 * ProfileActivity
 *
 * Displays the user's profile:
 *  - Personal info card (avatar, name, email, membership badge, quick stats)
 *  - Grouped settings menus (iOS-style)
 *  - Floating bottom navigation bar
 */
public class ProfileActivity extends AppCompatActivity {

    // ── Profile Card ──────────────────────────────────────────────────
    private ImageView ivAvatar;
    private View btnEditProfile;
    private TextView  tvProfileName;
    private TextView  tvProfileEmail;
    private TextView  tvMembershipLabel;
    private TextView  tvStreak;
    private TextView  tvXp;
    private TextView  tvHours;

    // ── Menu Rows ─────────────────────────────────────────────────────
    private LinearLayout rowCertificates;
    private LinearLayout rowDownloads;
    private LinearLayout rowOrderHistory;
    private LinearLayout rowVouchers;
    private LinearLayout rowPaymentMethods;
    private LinearLayout rowHelpCenter;
    private LinearLayout rowLogout;

    // ── Toggles ───────────────────────────────────────────────────────
    private SwitchCompat switchNotifications;
    private SwitchCompat switchDarkMode;

    // ── Demo stat data ─────────────────────────────────────────────────
    private static final int STREAK_DAYS  = 14;
    private static final int XP_POINTS    = 2_450;
    private static final int LEARN_HOURS  = 38;

    private UserDto currentUserDto;
    
    private Uri selectedAvatarUri = null;
    private ImageView dialogAvatarView = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedAvatarUri = result.getData().getData();
                    if (dialogAvatarView != null) {
                        Glide.with(this)
                                .load(selectedAvatarUri)
                                .circleCrop()
                                .into(dialogAvatarView);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bindViews();
        // Fallback demo data
        populateUserData();
        
        // Fetch real data
        fetchUserProfile();

        setupMenuListeners();
        setupEditProfileListener();
        setupToggles();

        NavHelper.setupNavigation(this, R.id.navProfile);
    }

    // ─────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────

    private void bindViews() {
        ivAvatar          = findViewById(R.id.ivAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvProfileName     = findViewById(R.id.tvProfileName);
        tvProfileEmail    = findViewById(R.id.tvProfileEmail);
        tvMembershipLabel = findViewById(R.id.tvMembershipLabel);
        tvStreak          = findViewById(R.id.tvStreak);
        tvXp              = findViewById(R.id.tvXp);
        tvHours           = findViewById(R.id.tvHours);

        rowCertificates  = findViewById(R.id.rowCertificates);
        rowDownloads     = findViewById(R.id.rowDownloads);
        rowOrderHistory  = findViewById(R.id.rowOrderHistory);
        rowVouchers      = findViewById(R.id.rowVouchers);
        rowPaymentMethods = findViewById(R.id.rowPaymentMethods);
        rowHelpCenter    = findViewById(R.id.rowHelpCenter);
        rowLogout        = findViewById(R.id.rowLogout);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode      = findViewById(R.id.switchDarkMode);
    }

    // ─────────────────────────────────────────────────────────────────
    // Populate with real / demo data
    // ─────────────────────────────────────────────────────────────────

    private void populateUserData() {
        UserManager user = UserManager.getInstance();

        // Avatar
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            avatarUrl = "https://i.pravatar.cc/150?u=my_user";
        }
        Glide.with(this)
                .load(avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(ivAvatar);

        // Name & email
        String email = user.getUserEmail();
        String name = user.getUserName();
        
        if (email != null && !email.isEmpty()) {
            tvProfileEmail.setText(email);
        }
        
        if (name != null && !name.isEmpty()) {
            tvProfileName.setText(name);
        } else if (email != null && !email.isEmpty()) {
            String namePart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            tvProfileName.setText(namePart);
        }

        // Membership badge
        boolean isPro = "PRO".equalsIgnoreCase(user.getUserRole()); // Assuming role string
        tvMembershipLabel.setText(isPro ? R.string.profile_badge_pro : R.string.profile_badge_basic);

        // Quick stats
        tvStreak.setText(String.valueOf(STREAK_DAYS));
        tvXp.setText(String.valueOf(XP_POINTS));
        tvHours.setText(String.valueOf(LEARN_HOURS));
    }

    private void fetchUserProfile() {
        UserService userService = RetrofitClient.getInstance().getUserService();
        if (userService == null) return;

        userService.getCurrentUser().enqueue(new Callback<ApiResponse<UserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentUserDto = response.body().getData();
                    UserManager.getInstance().saveUserInfo(
                            currentUserDto.getUserId(),
                            currentUserDto.getEmail(),
                            currentUserDto.getName(),
                            currentUserDto.getAvatarUrl(),
                            currentUserDto.getRole()
                    );
                    runOnUiThread(() -> updateProfileUI(currentUserDto));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                Log.e("ProfileActivity", "Failed to fetch user profile", t);
            }
        });
    }

    private void updateProfileUI(UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            tvProfileName.setText(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            tvProfileEmail.setText(userDto.getEmail());
            UserManager.getInstance().setUserEmail(userDto.getEmail());
        }
        if (userDto.getAvatarUrl() != null && !userDto.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(userDto.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(ivAvatar);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Menu click listeners
    // ─────────────────────────────────────────────────────────────────

        private void setupEditProfileListener() {
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        }
    }

    private void showEditProfileDialog() {
        selectedAvatarUri = null;
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_profile_edit_bottom_sheet, null);
        dialog.setContentView(view);
        
        dialogAvatarView = view.findViewById(R.id.editAvatar);
        if (dialogAvatarView != null) {
            String currentAvatar = UserManager.getInstance().getAvatarUrl();
            if (currentAvatar != null && !currentAvatar.isEmpty()) {
                Glide.with(this).load(currentAvatar).circleCrop().into(dialogAvatarView);
            }
            view.findViewById(R.id.editAvatar).setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });
            // Try to find the inner LL that has both avatar and text
            View avatarContainer = (View) dialogAvatarView.getParent();
            if (avatarContainer != null && avatarContainer.getParent() instanceof View) {
                ((View) avatarContainer.getParent()).setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imagePickerLauncher.launch(intent);
                });
            }
        }

        EditText inputName = view.findViewById(R.id.inputName);
        EditText inputEmail = view.findViewById(R.id.inputEmail);
        View btnSave = view.findViewById(R.id.btnSaveProfile);
        View btnClose = view.findViewById(R.id.btnClose);
        
        inputName.setText(tvProfileName.getText());
        inputEmail.setText(tvProfileEmail.getText());
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newName = inputName.getText().toString();
            String newEmail = inputEmail.getText().toString(); // Maybe changing email needs auth change, we'll try API anyway
            if (!newName.isEmpty()) {
                
                UserService userService = RetrofitClient.getInstance().getUserService();
                if (userService != null && UserManager.getInstance().getUserId() != null) {
                    UpdateProfileRequest updateReq = new UpdateProfileRequest();
                    updateReq.setName(newName);
                    
                    String json = new Gson().toJson(updateReq);
                    RequestBody userBody = RequestBody.create(MediaType.parse("application/json"), json);
                    MultipartBody.Part avatarPart = prepareAvatarPart(selectedAvatarUri);
                    
                    userService.updateUser(UserManager.getInstance().getUserId(), userBody, avatarPart).enqueue(new Callback<ApiResponse<UserDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<UserDto>> call, Response<ApiResponse<UserDto>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                currentUserDto = response.body().getData();
                                UserManager.getInstance().setUserName(currentUserDto.getName());
                                runOnUiThread(() -> {
                                    updateProfileUI(currentUserDto);
                                    ToastUtil.showCustomToast(ProfileActivity.this, "Cập nhật hồ sơ thành công");
                                    dialog.dismiss();
                                });
                            } else {
                                ToastUtil.showCustomToast(ProfileActivity.this, "Cập nhật thất bại");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<UserDto>> call, Throwable t) {
                            Log.e("ProfileActivity", "Failed to update profile", t);
                            ToastUtil.showCustomToast(ProfileActivity.this, "Lỗi kết nối");
                        }
                    });
                } else {
                    tvProfileName.setText(newName);
                    UserManager.getInstance().setUserName(newName);
                    dialog.dismiss();
                }
            } else {
                ToastUtil.showCustomToast(this, "Tên không được để trống");
            }
        });
        
        dialog.show();
    }
    
    private MultipartBody.Part prepareAvatarPart(Uri uri) {
        if (uri == null) return null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] bytes = buffer.toByteArray();
                String mimeType = getContentResolver().getType(uri);
                if (mimeType == null) mimeType = "image/jpeg";
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
                return MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestFile);
            }
        } catch (IOException e) {
            Log.e("ProfileActivity", "Error preparing avatar part", e);
        }
        return null;
    }

    private void setupMenuListeners() {
        // Learning group
        rowCertificates.setOnClickListener(v -> {
            Intent intent = new Intent(this, CertificatesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowDownloads.setOnClickListener(v ->
                ToastUtil.showCustomToast(this, getString(R.string.profile_coming_soon)));

        // Transactions group
        rowOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, PurchaseHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowVouchers.setOnClickListener(v -> {
            Intent intent = new Intent(this, VouchersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        rowPaymentMethods.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentMethodsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        // Support group
        rowHelpCenter.setOnClickListener(v ->
                ToastUtil.showCustomToast(this, getString(R.string.profile_coming_soon)));

        rowLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    // ─────────────────────────────────────────────────────────────────
    // Toggle listeners
    // ─────────────────────────────────────────────────────────────────

    private void setupToggles() {
        switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
            String msg = isChecked
                    ? getString(R.string.profile_notifications_on)
                    : getString(R.string.profile_notifications_off);
            ToastUtil.showCustomToast(this, msg);
        });

        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            String msg = isChecked
                    ? getString(R.string.profile_dark_mode_on)
                    : getString(R.string.profile_dark_mode_off);
            ToastUtil.showCustomToast(this, msg);
            // TODO: apply AppCompatDelegate.setDefaultNightMode() when dark theme assets are ready
        });
    }

    // ─────────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────────

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_logout)
                .setMessage(R.string.profile_logout_confirm)
                .setPositiveButton(R.string.profile_logout_yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.profile_logout_cancel, null)
                .show();
    }

    private void performLogout() {
        UserManager.getInstance().logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
