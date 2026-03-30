package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface EnrollmentService {
    @GET("api/v1/vouchers/{id}")
    Call<ApiResponse<Object>> getVoucherById(@Path("id") String id);

    @PUT("api/v1/vouchers/{id}")
    Call<ApiResponse<Object>> updateVoucher(@Path("id") String id, @Body UpdateVoucherRequest body);

    @DELETE("api/v1/vouchers/{id}")
    Call<ApiResponse<Object>> deleteVoucher(@Path("id") String id);

    @GET("api/v1/vouchers")
    Call<PaginatedApiResponseObject> getVouchers(@Query("query") PaginatedApiQuery query);

    @POST("api/v1/vouchers")
    Call<ApiResponse<Object>> createVoucher(@Body CreateVoucherRequest body);

    @GET("api/v1/orders")
    Call<PaginatedApiResponseObject> getOrders();

    @POST("api/v1/orders")
    Call<ApiResponse<Object>> createOrder(@Body CreateOrderRequest body);

    @POST("api/v1/enrollments/check")
    Call<ApiResponse<Object>> checkEnrollmentStatus(@Body List<String> body);

    @GET("api/v1/vouchers/code")
    Call<ApiResponse<Object>> getVoucherByCode(@Query("code") String code);

    @GET("api/v1/orders/{orderId}")
    Call<ApiResponse<Object>> getOrderById(@Path("orderId") String orderId);

    @GET("api/v1/enrollments")
    Call<PaginatedApiResponseEnrollmentResponse> getEnrolledCourses(@Query("page") Integer page, @Query("size") Integer size);

}
