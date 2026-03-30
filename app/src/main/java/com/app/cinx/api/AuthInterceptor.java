package com.app.cinx.api;

import com.app.cinx.utils.TokenManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();

        // Do not intercept auth endpoints
        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh-token") || path.contains("/auth/send-otp") || path.contains("/auth/verify-otp")) {
            return chain.proceed(request);
        }

        TokenManager tokenManager = TokenManager.getInstance();
        if (tokenManager.hasToken()) {
            request = request.newBuilder()
                    .header("Authorization", tokenManager.getBearerToken())
                    .build();
        }

        return chain.proceed(request);
    }
}

