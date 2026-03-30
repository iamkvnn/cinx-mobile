package com.app.cinx.api;

import com.app.cinx.api.dto.RecommendationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RecommendationService {
    @GET("api/v1/recommendations/users/{userId}")
    Call<RecommendationResponse> getRecommendations(@Path("userId") String userId);
}

