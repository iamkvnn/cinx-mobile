package com.app.cinx.api;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.RefreshTokenRequest;
import com.app.cinx.api.dto.TokenResponseDto;
import com.app.cinx.utils.TokenManager;
import com.app.cinx.utils.UserManager;
import com.app.cinx.CinxApp;
import com.app.cinx.activity.LoginActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
public class TokenAuthenticator implements Authenticator {
    private AuthService authService;
    private synchronized AuthService getAuthService() {
        if (authService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.shinyjewelry.shop/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            authService = retrofit.create(AuthService.class);
        }
        return authService;
    }
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        TokenManager tokenManager = TokenManager.getInstance();
        if (tokenManager.getRefreshToken() == null || tokenManager.getRefreshToken().isEmpty()) {
            tokenManager.clear();
            return null;
        }
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setToken(tokenManager.getRefreshToken());
        Call<ApiResponse<TokenResponseDto>> call = getAuthService().refreshToken(request);
        retrofit2.Response<ApiResponse<TokenResponseDto>> refreshResponse = call.execute();
        if (refreshResponse.isSuccessful() && refreshResponse.body() != null && refreshResponse.body().getData() != null) {
            TokenResponseDto newTokens = refreshResponse.body().getData();
            tokenManager.saveTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());
            return response.request().newBuilder()
                    .header("Authorization", tokenManager.getBearerToken())
                    .build();
        } else {
            handleLogout();
            return null;
        }
    }

    private void handleLogout() {
        UserManager.getInstance().logout();
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(CinxApp.getInstance(), "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CinxApp.getInstance(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            CinxApp.getInstance().startActivity(intent);
        });
    }
}
