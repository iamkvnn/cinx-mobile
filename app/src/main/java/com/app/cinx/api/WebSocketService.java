package com.app.cinx.api;
import android.util.Log;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import java.util.HashMap;
import io.reactivex.disposables.Disposable;
public class WebSocketService {
    private static final String TAG = "WebSocketService";
    private static final String WEBSOCKET_URL = "ws://10.0.2.2:9090/ws";
    private static WebSocketService instance;
    private StompClient stompClient;
    private Disposable lifecycleDisposable;
    private WebSocketService() {}
    public static synchronized WebSocketService getInstance() {
        if (instance == null) {
            instance = new WebSocketService();
        }
        return instance;
    }
    public void connect() {
        if (stompClient != null && stompClient.isConnected()) {
            return;
        }
        // Use the common OkHttpClient that has our AuthInterceptor and TokenAuthenticator
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL, new HashMap<>(), RetrofitClient.getInstance().getHttpClient());
        lifecycleDisposable = stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.i(TAG, "Stomp connection opened");
                    break;
                case ERROR:
                    Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.i(TAG, "Stomp connection closed");
                    break;
            }
        });
        stompClient.connect();
    }
    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed()) {
            lifecycleDisposable.dispose();
        }
    }
    public StompClient getStompClient() {
        return stompClient;
    }
}
