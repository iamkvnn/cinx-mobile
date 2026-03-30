package com.app.cinx.api;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.RefreshTokenRequest;
import com.app.cinx.api.dto.TokenResponseDto;
import com.app.cinx.utils.TokenManager;
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
                    .baseUrl("http://10.0.2.2:9090/")
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
            tokenManager.clear();
            return null;
        }
    }
}
