package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface EnrollmentService {
    @GET("api/v1/vouchers/{id}")
    Call<ApiResponse<VoucherResponse>> getVoucherById(@Path("id") String id);

    @PUT("api/v1/vouchers/{id}")
    Call<ApiResponse<Object>> updateVoucher(@Path("id") String id, @Body UpdateVoucherRequest body);

    @DELETE("api/v1/vouchers/{id}")
    Call<ApiResponse<Object>> deleteVoucher(@Path("id") String id);

    @GET("api/v1/vouchers")
    Call<PaginatedApiResponseVoucherResponse> getVouchers(@Query("page") int page, @Query("size") int size);

    @POST("api/v1/vouchers")
    Call<ApiResponse<Object>> createVoucher(@Body CreateVoucherRequest body);

    @GET("api/v1/orders")
    Call<PaginatedApiResponseOrderDetailResponse> getOrders();

    @POST("api/v1/orders")
    Call<ApiResponse<OrderResponse>> createOrder(@Body CreateOrderRequest body);

    @POST("api/v1/enrollments/check")
    Call<ApiResponse<List<CheckEnrollmentStatus>>> checkEnrollmentStatus(@Body List<String> body);

    @GET("api/v1/vouchers/code")
    Call<ApiResponse<VoucherResponse>> getVoucherByCode(@Query("code") String code);

    @GET("api/v1/orders/{orderId}")
    Call<ApiResponse<OrderDetailResponse>> getOrderById(@Path("orderId") String orderId);

    @GET("api/v1/enrollments")
    Call<PaginatedApiResponseCourseResponse> getEnrolledCourses(@Query("page") Integer page, @Query("size") Integer size);

}