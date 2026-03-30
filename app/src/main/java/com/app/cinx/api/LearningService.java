package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface LearningService {
    @GET("api/v1/learning/video-tracking")
    Call<PaginatedApiResponseObject> getVideoLessonTrackingHistories(@Query("videoLessonId") String videoLessonId, @Query("page") Integer page, @Query("size") Integer size);

    @POST("api/v1/learning/video-tracking")
    Call<ApiResponse<Object>> trackVideoProgress(@Body TrackingVideoLessonRequest body);

    @GET("api/v1/learning/quiz-sessions")
    Call<PaginatedApiResponseObject> getQuizSessions(@Query("userId") String userId, @Query("quizLessonId") String quizLessonId, @Query("page") Integer page, @Query("size") Integer size);

    @POST("api/v1/learning/quiz-sessions")
    Call<ApiResponse<Object>> createQuizSession(@Query("quizLessonId") String quizLessonId);

    @POST("api/v1/learning/quiz-sessions/{quizSessionId}/submit")
    Call<ApiResponse<Object>> submitQuizSession(@Path("quizSessionId") String quizSessionId, @Body SubmitQuizSessionRequest body);

    @POST("api/v1/learning/quiz-sessions/{quizSessionId}/choose")
    Call<ApiResponse<Object>> chooseQuizSessionQuestion(@Path("quizSessionId") String quizSessionId, @Body ChooseQuizAnswerRequest body);

    @GET("api/v1/learning/assignment-submissions")
    Call<ApiResponse<Object>> getAssignmentSubmission(@Query("assignmentId") String assignmentId);

    @POST("api/v1/learning/assignment-submissions")
    Call<ApiResponse<Object>> submitAssignment(@Query("assignmentId") String assignmentId, @Body CreateAssignmentSubmissionRequest body);

    @POST("api/v1/learning/assignment-submissions/{submissionId}/score")
    Call<ApiResponse<Object>> scoreAssignmentSubmission(@Path("submissionId") String submissionId, @Query("score") Double score);

    @GET("api/v1/learning/video-tracking/history")
    Call<ApiResponse<Object>> getVideoLessonTrackingHistory(@Query("videoLessonId") String videoLessonId);

    @GET("api/v1/learning/upload/presigned-url")
    Call<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@Query("fileName") String fileName, @Query("contentType") String contentType);

    @GET("api/v1/learning/quiz-sessions/{quizSessionId}")
    Call<ApiResponse<Object>> getQuizSession(@Path("quizSessionId") String quizSessionId);

    @GET("api/v1/learning/quiz-sessions/{quizSessionId}/questions")
    Call<PaginatedApiResponseObject> getQuizSessionQuestions(@Path("quizSessionId") String quizSessionId, @Query("page") Integer page, @Query("size") Integer size);

    @GET("api/v1/learning/course-progress")
    Call<ApiResponse<Object>> getCourseProgressByCourseIds(@Query("courseIds") List<String> courseIds);

    @GET("api/v1/learning/course-progress/{courseId}")
    Call<ApiResponse<Object>> getCourseProgress(@Path("courseId") String courseId);

    @GET("api/v1/learning/course-progress/{courseId}/items")
    Call<ApiResponse<Object>> getLearningItemProgressByCourseId(@Path("courseId") String courseId);

    @GET("api/v1/learning/assignment-submissions/list")
    Call<PaginatedApiResponseObject> getAssignmentSubmissions(@Query("assignmentId") String assignmentId, @Query("page") Integer page, @Query("size") Integer size);

    @DELETE("api/v1/learning/assignment-submissions/{submissionId}")
    Call<ApiResponse<Object>> deleteAssignmentSubmission(@Path("submissionId") String submissionId);

}
