package com.app.cinx.api;

import com.app.cinx.api.dto.ApiListResponse;
import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.CategoryDto;
import com.app.cinx.api.dto.CourseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/** Course & category endpoints — base URL: http://localhost:9090 */
public interface CourseService {

    /**
     * @param page  1-based page number
     * @param size  items per page
     * @param sort  URL-encoded JSON sort object, e.g. {"discountedPrice":"asc"}
     */
    @GET("api/v1/courses")
    Call<ApiListResponse<CourseDto>> getCourses(
            @Header("Authorization") String bearerToken,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort);

    @GET("api/v1/categories")
    Call<ApiResponse<java.util.List<CategoryDto>>> getCategories(
            @Header("Authorization") String bearerToken);
}
