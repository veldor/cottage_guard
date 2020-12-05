package net.veldor.cottage_guard.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import net.veldor.cottage_guard.App;

public class MyPreferences {

    private static final String PREFERENCE_SERVER_IP = "server_ip";
    private static final String PREFERENCE_SERVER_PORT = "server_port";
    private static final String PREFERENCE_FIREBASE_TOKEN = "firebase token";
    private static MyPreferences instance;
    private final SharedPreferences mSharedPreferences;

    public static MyPreferences getInstance() {
        if (instance == null) {
            instance = new MyPreferences();
        }
        return instance;
    }

    private MyPreferences() {
        // читаю настройки sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }

    public String getServerIP(){
        return mSharedPreferences.getString(PREFERENCE_SERVER_IP, "127.0.0.1");
    }
    public int getServerPort(){
        return Integer.parseInt(mSharedPreferences.getString(PREFERENCE_SERVER_PORT, "8843"));
    }

    public void setFirebaseToken(String token) {
        mSharedPreferences.edit().putString(PREFERENCE_FIREBASE_TOKEN, token).apply();
    }

    public String getFirebaseToken() {
        return mSharedPreferences.getString(PREFERENCE_FIREBASE_TOKEN, null);
    }
}
