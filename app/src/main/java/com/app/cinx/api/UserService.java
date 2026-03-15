package com.app.cinx.api;

import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.UserProfileDto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/** User profile endpoints — base URL: http://localhost:8089 */
public interface UserService {

    @GET("api/v1/users/me")
    Call<ApiResponse<UserProfileDto>> getProfile(
            @Header("Authorization") String bearerToken);

    /**
     * Update user profile with optional avatar.
     * The "user" part is a JSON blob: {"name":"...","gender":"..."}.
     * The "avatar" part is optional (pass null to skip).
     */
    @Multipart
    @PUT("api/v1/users/{id}")
    Call<ApiResponse<UserProfileDto>> updateProfile(
            @Header("Authorization") String bearerToken,
            @Path("id") String userId,
            @Part("user") RequestBody userJson,
            @Part MultipartBody.Part avatar);
}
