package net.veldor.cottage_guard;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import net.veldor.cottage_guard.selections.DefenceAlert;
import net.veldor.cottage_guard.socket.SocketConnection;
import net.veldor.cottage_guard.utils.FirebaseHandler;
import net.veldor.cottage_guard.utils.MyNotify;
import net.veldor.cottage_guard.utils.MyPreferences;
import net.veldor.cottage_guard.workers.WebSocketCheckWorker;

public class App extends Application {

    private static App instance;
    public MutableLiveData<DefenceAlert> mLiveCurrentAlert = new MutableLiveData<>();
    private MyNotify mNotifier;
    public SocketConnection mSocket;
    public MutableLiveData<Boolean> mLiveSocketConnected = new MutableLiveData<>(false);
    public static App getInstance() {
            return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mNotifier = new MyNotify();
        startSocket();
        // запущу рабочего, который будет следить за websocket-ом
        startMainWorker();
        checkFirebaseConnection();

    }

    private void checkFirebaseConnection() {
        if(MyPreferences.getInstance().getFirebaseToken() == null){
            Log.d("surprise", "App onCreate 50: getting token");
            (new FirebaseHandler()).getToken();
        }
        else{
            Log.d("surprise", "App onCreate 54: token is " + MyPreferences.getInstance().getFirebaseToken());
        }
    }

    public void startMainWorker(){
        if(!App.getInstance().mLiveSocketConnected.getValue()){
            App.getInstance().startSocket();
        }
        // запущу рабочего, который каждые 10 секунд будет обновлять данные
        OneTimeWorkRequest checkStatusWork = new OneTimeWorkRequest.Builder(WebSocketCheckWorker.class).addTag(WebSocketCheckWorker.TAG).build();
        WorkManager.getInstance(App.this).enqueueUniqueWork(WebSocketCheckWorker.TAG, ExistingWorkPolicy.REPLACE, checkStatusWork);
    }

    public boolean isSocketConnected() {
        return mSocket.isConnected();
    }

    public void reconnect() {
        Log.d("surprise", "App reconnect 65: reconnect");
        mSocket.openConnection();
    }

    public MyNotify getNotifier() {
        return mNotifier;
    }

    public void startSocket() {
        mSocket = new SocketConnection();
        mSocket.openConnection();
    }

    public void sendPing() {
        Log.d("surprise", "App sendPing 82: sending ping");
        mSocket.sendMessage("ping");
    }
}
