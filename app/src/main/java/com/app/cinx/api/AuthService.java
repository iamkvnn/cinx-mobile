package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface AuthService {
    @POST("api/v1/auth/verify-otp")
    Call<ApiResponse<Object>> verifyOtp(@Body VerifyEmailRequest body);

    @POST("api/v1/auth/send-otp")
    Call<ApiResponse<Object>> resendOtp(@Body SendOtpRequest body);

    @POST("api/v1/auth/send-change-password-otp")
    Call<ApiResponse<Object>> sendChangePasswordOtp(@Body SendOtpRequest body);

    @POST("api/v1/auth/send-change-email-otp")
    Call<ApiResponse<Object>> sendChangeEmailOtp(@Body SendOtpRequest body);

    @POST("api/v1/auth/reset-password")
    Call<ApiResponse<Object>> resetPassword(@Body ResetPasswordRequest body);

    @POST("api/v1/auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest body);

    @POST("api/v1/auth/refresh-token")
    Call<ApiResponse<TokenResponseDto>> refreshToken(@Body RefreshTokenRequest body);

    @POST("api/v1/auth/login")
    Call<ApiResponse<TokenResponseDto>> login(@Body AuthRequestDto body);

    @POST("api/v1/auth/change-password")
    Call<ApiResponse<Object>> changePassword(@Body ChangePasswordRequest body);

    @POST("api/v1/auth/change-email")
    Call<ApiResponse<Object>> changeEmail(@Body ChangeEmailRequest body);

}