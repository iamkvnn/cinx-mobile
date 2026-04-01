package com.app.cinx.api;

import com.app.cinx.api.dto.*;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface UserService {
    @GET("api/v1/users/{id}")
    Call<ApiResponse<UserDto>> getUserById(@Path("id") String id);

    @PUT("api/v1/users/{id}")
    Call<ApiResponse<UserDto>> updateUser(
            @Path("id") String id,
            @Part("user") RequestBody user,
            @Part MultipartBody.Part avatar
    );

    @GET("api/v1/users")
    Call<PaginatedApiResponseUserDto> getAllUsers(@Query("page") Integer page, @Query("size") Integer size);

    @GET("api/v1/users")
    Call<PaginatedApiResponseUserDto> getUsersByRole(@Query("page") Integer page, @Query("size") Integer size, @Query("role") String role);

    @GET("api/v1/instructors")
    Call<PaginatedApiResponseUserDto> getInstructors(@Query("page") Integer page, @Query("size") Integer size);

    @POST("api/v1/users")
    Call<ApiResponse<UserDto>> createUser(@Body CreateUserRequest body);

    @POST("api/v1/users/{userId}/add-xp")
    Call<ApiResponse<UserDto>> addXp(@Path("userId") String userId, @Query("amount") Integer amount);

    @POST("api/v1/users/{id}/verify-instructor")
    Call<ApiResponse<Object>> verifyInstructor(@Path("id") String id);

    @PUT("api/v1/instructors/{id}/verify")
    Call<ApiResponse<Object>> verifyInstructorByPut(@Path("id") String id, @Body VerifyInstructorRequest body);

    @POST("api/v1/users/device-tokens")
    Call<ApiResponse<Void>> saveDeviceToken(@Body DeviceTokenRequest body);

    @GET("api/v1/users/{userId}/fcm-tokens")
    Call<ApiResponse<List<String>>> getUserTokens(@Path("userId") String userId);

    @GET("api/v1/users/{id}/instructor-verified")
    Call<ApiResponse<Boolean>> checkInstructorVerified(@Path("id") String id);

    @GET("api/v1/users/me")
    Call<ApiResponse<UserDto>> getCurrentUser();

    @GET("api/v1/users/ids")
    Call<ApiResponse<List<UserDto>>> getUsersByIds(@Query("ids") List<String> ids);

    @GET("api/v1/users/avatars/{fileName}")
    Call<String> getAvatarImage(@Path("fileName") String fileName);

}