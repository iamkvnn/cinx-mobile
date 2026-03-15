package com.app.cinx.api;

import com.app.cinx.api.dto.AddToCartRequest;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CartItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/** Cart endpoints — base URL: http://localhost:9090 */
public interface CartService {

    @POST("api/v1/cart")
    Call<ApiResponse<Void>> addToCart(
            @Header("Authorization") String bearerToken,
            @Body AddToCartRequest request);

    @GET("api/v1/cart")
    Call<ApiResponse<List<CartItemDto>>> getCart(
            @Header("Authorization") String bearerToken);

    @DELETE("api/v1/cart/{id}")
    Call<ApiResponse<Void>> removeItem(
            @Header("Authorization") String bearerToken,
            @Path("id") String cartItemId);

    @DELETE("api/v1/cart/clear")
    Call<ApiResponse<Void>> clearCart(
            @Header("Authorization") String bearerToken);
}
