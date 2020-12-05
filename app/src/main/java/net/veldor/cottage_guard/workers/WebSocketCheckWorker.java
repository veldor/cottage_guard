package net.veldor.cottage_guard.workers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.veldor.cottage_guard.App;
import net.veldor.cottage_guard.utils.MyNotify;

public class WebSocketCheckWorker extends Worker {
    public static final String TAG = "check webSocket";

    public WebSocketCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyNotify notifier = App.getInstance().getNotifier();
        Notification notification;
        notification = notifier.createCheckingNotification();
        int notificationCode = MyNotify.CHECKER_NOTIFICATION;
        // помечу рабочего важным
        ForegroundInfo info = new ForegroundInfo(notificationCode, notification);
        setForegroundAsync(info);
        if (App.getInstance().mSocket == null) {
            App.getInstance().startSocket();
        }
        while (true) {
            Log.d("surprise", "WebSocketCheckWorker doWork 35: check connection");
            // каждые 15 секунд буду проверять соединение с сокетом
            if(!App.getInstance().isSocketConnected()){
                Log.d("surprise", "WebSocketCheckWorker doWork 39: here");
                App.getInstance().mLiveSocketConnected.postValue(false);
                App.getInstance().reconnect();
                Log.d("surprise", "WebSocketCheckWorker doWork 40: no socket connection, reconnect");
            }
            else{
                Log.d("surprise", "WebSocketCheckWorker doWork 45: i here");
                // напомню, что я онлайн
                App.getInstance().sendPing();
                Log.d("surprise", "WebSocketCheckWorker doWork 42: connected");
            }
            try {
                //noinspection BusyWait
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            if(isStopped()){
                return Result.success();
            }
        }
        return Result.success();
    }
}
