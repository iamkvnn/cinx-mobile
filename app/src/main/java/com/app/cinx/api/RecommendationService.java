package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface RecommendationService {
    @GET("api/v1/recommendations/users/{userId}")
    Call<RecommendationResponse> getRecommendations(@Path("userId") String userId);

    @POST("api/v1/recommendations/learning-path/generate")
    Call<GenerateLearningPathResponse> generateLearningPath(@Body GenerateLearningPathRequest body);

}