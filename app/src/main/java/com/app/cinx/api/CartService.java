package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface CartService {
    @GET("api/v1/cart")
    Call<ApiResponse<List<CartItemResponse>>> getCart();

    @POST("api/v1/cart")
    Call<ApiResponse<Void>> addToCart(@Body AddToCartRequest body);

    @DELETE("api/v1/cart/{itemId}")
    Call<ApiResponse<Void>> removeFromCart(@Path("itemId") String itemId);

    @DELETE("api/v1/cart/ids")
    Call<ApiResponse<Void>> removeFromCart_1(@Query("itemIds") List<String> itemIds);

    @DELETE("api/v1/cart/clear")
    Call<ApiResponse<Void>> clearCart();

}