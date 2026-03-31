package com.app.cinx.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit factory.
 *
 * Three backend base URLs are used:
 *   MAIN  (port 9090) — auth, courses, cart, orders, enrollments, payments
 *
 * Service instances are cached after first creation.
 */
public class RetrofitClient {

    private static final String BASE_URL = "http://api.shinyjewelry.shop/";

    private static RetrofitClient instance;

    private final Retrofit retrofitMain;
    private final OkHttpClient httpClient;

    // cached service singletons
    private AuthService         authService;
    private UserService         userService;
    private CourseService       courseService;
    private CartService         cartService;
    private OrderService        orderService;
    private EnrollmentService   enrollmentService;
    private PaymentService      paymentService;
    private LearningService     learningService;
    private NotificationService  notificationService;
    private RecommendationService recommendationService;
    private SocialService      socialService;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .authenticator(new TokenAuthenticator())
                .addInterceptor(logging)
                .build();

        GsonConverterFactory gsonFactory = GsonConverterFactory.create();

        retrofitMain = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(gsonFactory)
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    // ── service accessors ────────────────────────────────────────────────

    public AuthService getAuthService() {
        if (authService == null) authService = retrofitMain.create(AuthService.class);
        return authService;
    }

    public UserService getUserService() {
        if (userService == null) userService = retrofitMain.create(UserService.class);
        return userService;
    }

    public CourseService getCourseService() {
        if (courseService == null) courseService = retrofitMain.create(CourseService.class);
        return courseService;
    }

    public CartService getCartService() {
        if (cartService == null) cartService = retrofitMain.create(CartService.class);
        return cartService;
    }

    public OrderService getOrderService() {
        if (orderService == null) orderService = retrofitMain.create(OrderService.class);
        return orderService;
    }

    public EnrollmentService getEnrollmentService() {
        if (enrollmentService == null)
            enrollmentService = retrofitMain.create(EnrollmentService.class);
        return enrollmentService;
    }

    public PaymentService getPaymentService() {
        if (paymentService == null) paymentService = retrofitMain.create(PaymentService.class);
        return paymentService;
    }

    public LearningService getLearningService() {
        if (learningService == null) learningService = retrofitMain.create(LearningService.class);
        return learningService;
    }

    public NotificationService getNotificationService() {
        if (notificationService == null) notificationService = retrofitMain.create(NotificationService.class);
        return notificationService;
    }

    public RecommendationService getRecommendationService() {
        if (recommendationService == null) recommendationService = retrofitMain.create(RecommendationService.class);
        return recommendationService;
    }

    public SocialService getSocialService() {
        if (socialService == null) socialService = retrofitMain.create(SocialService.class);
        return socialService;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }
}
