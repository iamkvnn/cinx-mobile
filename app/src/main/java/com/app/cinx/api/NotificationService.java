package com.app.cinx.api;

import com.app.cinx.api.dto.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface NotificationService {
    @POST("api/v1/notifications/{notificationId}/toggle-read")
    Call<ApiResponse<Void>> toggleRead(@Path("notificationId") String notificationId);

    @GET("api/v1/notifications")
    Call<PaginatedApiResponseUserNotificationResponse> getNotifications(@Query("page") Integer page, @Query("size") Integer size);

    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Long>> countUnreadNotifications();

    @DELETE("api/v1/notifications/{notificationId}")
    Call<ApiResponse<Void>> deleteNotification(@Path("notificationId") String notificationId);

}