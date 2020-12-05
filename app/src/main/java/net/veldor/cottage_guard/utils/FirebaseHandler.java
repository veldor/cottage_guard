package net.veldor.cottage_guard.utils;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import net.veldor.cottage_guard.App;

public class FirebaseHandler {
    public void getToken() {
        // проверю наличие токена Firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("surprise", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    if (token != null) {
                        Log.d("surprise", "FirebaseHandler getToken 24: save token " + token);
                        MyPreferences.getInstance().setFirebaseToken(token);
                        FirebaseMessaging.getInstance().subscribeToTopic("alerts").addOnSuccessListener(aVoid -> {
                            Toast.makeText(App.getInstance(), "Subscribed to alerts", Toast.LENGTH_LONG).show();
                            Log.d("surprise", "FirebaseHandler getToken 25: subscribed to alerts");
                        });
                    }
                });
    }
}
