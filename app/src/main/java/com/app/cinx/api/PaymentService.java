package com.app.cinx.api;

import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.GetPaymentUrlRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Payment endpoints — base URL: http://localhost:9090 */
public interface PaymentService {

    /** Returns a redirect URL for the chosen payment gateway. */
    @POST("api/v1/payments")
    Call<ApiResponse<String>> getPaymentUrl(
            @Header("Authorization") String bearerToken,
            @Body GetPaymentUrlRequest request);
}
