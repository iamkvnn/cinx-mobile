package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface SocialService {
    @PUT("api/v1/reviews/{reviewId}")
    Call<ApiResponse<Object>> updateReview(@Path("reviewId") String reviewId, @Body UpdateReviewRequest body);

    @DELETE("api/v1/reviews/{reviewId}")
    Call<ApiResponse<Object>> deleteReview(@Path("reviewId") String reviewId);

    @GET("api/v1/wishlist")
    Call<ApiResponse<List<WishlistItemResponse>>> getWishlist();

    @POST("api/v1/wishlist")
    Call<ApiResponse<Object>> addToWishlist(@Body AddToWishlistRequest body);

    @DELETE("api/v1/wishlist")
    Call<ApiResponse<Object>> removeFromWishlist(@Query("courseId") String courseId);

    @GET("api/v1/reviews")
    Call<ApiResponse<List<ReviewResponse>>> getReviewsByCourseId(@Query("courseId") String courseId);

    @POST("api/v1/reviews")
    Call<ApiResponse<Object>> createReview(@Body CreateReviewRequest body);

    @POST("api/v1/reviews/{reviewId}/report")
    Call<ApiResponse<Object>> reportReview(@Path("reviewId") String reviewId, @Body CreateReportReviewRequest body);

    @POST("api/v1/reviews/{reviewId}/react")
    Call<ApiResponse<Object>> reactReview(@Path("reviewId") String reviewId, @Body CreateReviewReactionRequest body);

}