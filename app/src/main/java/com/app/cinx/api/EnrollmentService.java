package com.app.cinx.api;

import com.app.cinx.api.dto.ApiResponse;
import com.app.cinx.api.dto.EnrollmentStatusDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Enrollment endpoints — base URL: http://localhost:9090 */
public interface EnrollmentService {

    /** Send a list of course IDs and get back enrollment status for each. */
    @POST("api/v1/enrollments/check")
    Call<ApiResponse<List<EnrollmentStatusDto>>> checkEnrollments(
            @Header("Authorization") String bearerToken,
            @Body List<String> courseIds);
}
