package com.app.cinx.api;

import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.AuthToken;
import com.app.cinx.api.dto.ChangePasswordRequest;
import com.app.cinx.api.dto.LoginRequest;
import com.app.cinx.api.dto.RefreshTokenRequest;
import com.app.cinx.api.dto.RegisterRequest;
import com.app.cinx.api.dto.ResetPasswordRequest;
import com.app.cinx.api.dto.SendOtpRequest;
import com.app.cinx.api.dto.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Auth endpoints — base URL: http://localhost:9090 */
public interface AuthService {

    @POST("api/v1/auth/login")
    Call<ApiResponse<AuthToken>> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<ApiResponse<Void>> register(@Body RegisterRequest request);

    @POST("api/v1/auth/send-otp")
    Call<ApiResponse<Void>> sendOtp(@Body SendOtpRequest request);

    @POST("api/v1/auth/verify-otp")
    Call<ApiResponse<Void>> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/v1/auth/refresh-token")
    Call<ApiResponse<AuthToken>> refreshToken(@Body RefreshTokenRequest request);

    @POST("api/v1/auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    @POST("api/v1/auth/change-password")
    Call<ApiResponse<Void>> changePassword(
            @Header("Authorization") String bearerToken,
            @Body ChangePasswordRequest request);
}
