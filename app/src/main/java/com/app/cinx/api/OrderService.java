package com.app.cinx.api;

import com.app.cinx.api.dto.ApiListResponse;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CreateOrderRequest;
import com.app.cinx.api.dto.OrderDetailDto;
import com.app.cinx.api.dto.OrderDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/** Order endpoints — base URL: http://localhost:9090 */
public interface OrderService {

    @POST("api/v1/orders")
    Call<ApiResponse<Void>> createOrder(
            @Header("Authorization") String bearerToken,
            @Body CreateOrderRequest request);

    @GET("api/v1/orders")
    Call<ApiListResponse<OrderDto>> getOrders(
            @Header("Authorization") String bearerToken);

    @GET("api/v1/orders/{id}")
    Call<ApiResponse<OrderDetailDto>> getOrder(
            @Header("Authorization") String bearerToken,
            @Path("id") String orderId);
}
