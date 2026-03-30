package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface PaymentService {
    @GET("api/v1/payments")
    Call<ApiResponse<PaymentResponse>> getPayment(@Query("orderId") String orderId, @Query("paymentMethod") String paymentMethod);

    @POST("api/v1/payments")
    Call<ApiResponse<String>> requestMomoPayment(@Body PaymentRequest body);

    @POST("api/v1/payments/momo-callback")
    Call<ApiResponse<Void>> handleMoMoCallback(@Body Object body);

    @GET("api/v1/payments/{paymentId}")
    Call<ApiResponse<PaymentResponse>> getPaymentById(@Path("paymentId") String paymentId);

    @GET("api/v1/payments/orders")
    Call<ApiResponse<List<PaymentResponse>>> getPaymentByOrderIds(@Query("orderIds") List<String> orderIds);

//    @POST("api/v1/payments/verify")
//    Call<ApiResponse<PaymentResponse>> verifyPayment(@Body PaymentVerifyRequest body);

    @GET("api/v1/payments/IPN")
    Call<VNPayIPNResponse> handleVNPayIPN();

}
