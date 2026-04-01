package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface CourseService {
    @GET("api/v1/video-lessons")
    Call<ApiResponse<VideoLessonResponse>> getVideoByLessonId(@Query("lessonId") String lessonId);

    @PUT("api/v1/video-lessons")
    Call<ApiResponse<Object>> updateVideoLesson(@Query("lessonId") String lessonId, @Body CreateVideoLessonRequest body);

    @POST("api/v1/video-lessons")
    Call<ApiResponse<Object>> createVideoLesson(@Query("lessonId") String lessonId, @Body CreateVideoLessonRequest body);

    @DELETE("api/v1/video-lessons")
    Call<ApiResponse<Object>> deleteVideoLesson(@Query("lessonId") String lessonId);

    @GET("api/v1/quiz-lessons")
    Call<ApiResponse<QuizLessonResponse>> getQuizByLessonId(@Query("lessonId") String lessonId);

    @PUT("api/v1/quiz-lessons")
    Call<ApiResponse<Object>> updateQuizLesson(@Query("lessonId") String lessonId, @Body CreateQuizLessonRequest body);

    @POST("api/v1/quiz-lessons")
    Call<ApiResponse<Object>> createQuizLesson(@Query("lessonId") String lessonId, @Body CreateQuizLessonRequest body);

    @DELETE("api/v1/quiz-lessons")
    Call<ApiResponse<Object>> deleteQuizLesson(@Query("lessonId") String lessonId);

    @GET("api/v1/courses/{id}")
    Call<ApiResponse<CourseDetailResponse>> getCourseById(@Path("id") String id);

    @PUT("api/v1/courses/{id}")
    Call<ApiResponse<CourseResponse>> updateCourse(@Path("id") String id, @Body UpdateCourseRequest body);

    @PUT("api/v1/courses/{courseId}/images/{imageId}")
    Call<ApiResponse<Object>> updateCourseImage(@Path("imageId") String imageId, @Path("courseId") String courseId, @Body UpdateCourseImageRequest body);

    @DELETE("api/v1/courses/{courseId}/images/{imageId}")
    Call<ApiResponse<Object>> deleteCourseImage(@Path("imageId") String imageId, @Path("courseId") String courseId);

    @PUT("api/v1/categories/{id}")
    Call<ApiResponse<Object>> updateCategory(@Path("id") String id, @Body UpdateCategoryRequest body);

    @GET("api/v1/assignment-lessons")
    Call<ApiResponse<AssignmentLessonResponse>> getAssigmentByLessonId(@Query("lessonId") String lessonId);

    @PUT("api/v1/assignment-lessons")
    Call<ApiResponse<Object>> updateAssigmentLesson(@Query("lessonId") String lessonId, @Body CreateAssignmentLessonRequest body);

    @POST("api/v1/assignment-lessons")
    Call<ApiResponse<Object>> createAssigmentLesson(@Query("lessonId") String lessonId, @Body CreateAssignmentLessonRequest body);

    @DELETE("api/v1/assignment-lessons")
    Call<ApiResponse<Object>> deleteAssigmentLesson(@Query("lessonId") String lessonId);

    @GET("api/v1/article-lessons")
    Call<ApiResponse<ArticleLessonResponse>> getArticleByLessonId(@Query("lessonId") String lessonId);

    @PUT("api/v1/article-lessons")
    Call<ApiResponse<Object>> updateArticleLesson(@Query("lessonId") String lessonId, @Body CreateArticleLessonRequest body);

    @POST("api/v1/article-lessons")
    Call<ApiResponse<Object>> createArticleLesson(@Query("lessonId") String lessonId, @Body CreateArticleLessonRequest body);

    @DELETE("api/v1/article-lessons")
    Call<ApiResponse<Object>> deleteArticleLesson(@Query("lessonId") String lessonId);

    @GET("api/v1/courses")
    Call<PaginatedApiResponseCourseResponse> getAllCourses(@Query("apiQuery") PaginatedApiQuery apiQuery, @Query("categoryId") String categoryId, @Query("instructorId") String instructorId);

    @POST("api/v1/courses")
    Call<ApiResponse<CourseResponse>> createCourse(@Body CreateCourseRequest body);

    @POST("api/v1/courses/{id}/update-rating")
    Call<ApiResponse<Void>> updateCourseRating(@Path("id") String id, @Query("rating") Double rating);

    @POST("api/v1/courses/{courseId}/images")
    Call<ApiResponse<Object>> uploadCourseImages(@Path("courseId") String courseId, @Body CreateCourseImageRequest body);

    @GET("api/v1/categories")
    Call<ApiResponse<List<CategoryResponse>>> getAllCategories();

    @POST("api/v1/categories")
    Call<ApiResponse<Object>> createCategory(@Body CreateCategoryRequest body);

    @GET("api/v1/courses/upload/presigned-url")
    Call<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@Query("fileName") String fileName, @Query("contentType") String contentType);

    @GET("api/v1/courses/ids")
    Call<ApiResponse<List<CourseResponse>>> getCourseById_1(@Query("ids") List<String> ids);

}