package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface UserService {
    @GET("api/v1/users/{id}")
    Call<ApiResponse<UserDto>> getUserById(@Path("id") String id);

    @PUT("api/v1/users/{id}")
    Call<ApiResponse<UserDto>> updateUser(@Path("id") String id);

    @GET("api/v1/users")
    Call<PaginatedApiResponseUserDto> getAllUsers(@Query("page") Integer page, @Query("size") Integer size);

    @POST("api/v1/users")
    Call<ApiResponse<UserDto>> createUser(@Body CreateUserRequest body);

    @POST("api/v1/users/{id}/verify-instructor")
    Call<ApiResponse<Object>> verifyInstructor(@Path("id") String id);

    @GET("api/v1/users/{id}/instructor-verified")
    Call<ApiResponse<Boolean>> checkInstructorVerified(@Path("id") String id);

    @GET("api/v1/users/me")
    Call<ApiResponse<UserDto>> getCurrentUser();

    @GET("api/v1/users/ids")
    Call<ApiResponse<List<UserDto>>> getUsersByIds(@Query("ids") List<String> ids);

    @GET("api/v1/users/avatars/{fileName}")
    Call<String> getAvatarImage(@Path("fileName") String fileName);

}