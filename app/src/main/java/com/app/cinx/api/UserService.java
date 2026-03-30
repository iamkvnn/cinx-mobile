package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface UserService {
    @PUT("api/v1/users/{id}")
    Call<ApiResponse<Object>> updateUser(@Path("id") String id);

    @POST("api/v1/users")
    Call<ApiResponse<Object>> createUser(@Body CreateUserRequest body);

    @GET("api/v1/users/me")
    Call<ApiResponse<UserDto>> getCurrentUser();

    @GET("api/v1/users/avatars/{fileName}")
    Call<String> getBannerImage(@Path("fileName") String fileName);

}
