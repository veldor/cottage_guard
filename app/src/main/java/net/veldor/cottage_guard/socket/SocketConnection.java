package net.veldor.cottage_guard.socket;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.veldor.cottage_guard.App;
import net.veldor.cottage_guard.R;
import net.veldor.cottage_guard.selections.AlertAcceptedAnswer;
import net.veldor.cottage_guard.selections.SocketAlertWrapper;
import net.veldor.cottage_guard.ui.Alarm;
import net.veldor.cottage_guard.utils.MyPreferences;

import java.util.Locale;


public class SocketConnection implements ClientWebSocket.MessageListener {
    private ClientWebSocket clientWebSocket;

    public void openConnection() {
        Log.d("surprise", "SocketConnection openConnection 42: opening connection");
        if (clientWebSocket != null) clientWebSocket.close();
        try {
            Log.d("surprise", "SocketConnection openConnection 24: connect to " + MyPreferences.getInstance().getServerIP() + ":" + MyPreferences.getInstance().getServerPort());
            clientWebSocket = new ClientWebSocket(this, "ws://" + MyPreferences.getInstance().getServerIP() + ":" + MyPreferences.getInstance().getServerPort());
            clientWebSocket.connect();
            App.getInstance().mLiveSocketConnected.postValue(true);
            System.out.println("i connected");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("can't connect to socket");
            App.getInstance().mLiveSocketConnected.postValue(false);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            openConnection();
        }
        //initScreenStateListener();
        //startCheckConnection();
    }


    @Override
    public void onSocketMessage(String message) {
        // проверю, если получена угроза- покажу окно
        if (message != null && message.startsWith("{\"command\":\"alert\"")) {
            // получу GSON
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            SocketAlertWrapper response = gson.fromJson(message, SocketAlertWrapper.class);
            Log.d("surprise", "SocketConnection onSocketMessage 64: alert on cottage " + response.alert.cottageNumber);
            App.getInstance().getNotifier().showAlertNotification(response.alert);
            App.getInstance().mLiveCurrentAlert.postValue(response.alert);
            Intent alarmIntent = new Intent(App.getInstance(), Alarm.class);
            alarmIntent.setClass(App.getInstance(), Alarm.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(alarmIntent);
            // отправлю сообщение, что угроза обработата
            AlertAcceptedAnswer resp = new AlertAcceptedAnswer();
            resp.alertTime = response.alert.actionTime;
            String answer = gson.toJson(resp);
            sendMessage(answer);
            Log.d("surprise", "SocketConnection onSocketMessage 68: alert handled message sent");
        }

    }


    public boolean isConnected() {
        return clientWebSocket != null &&
                clientWebSocket.getConnection() != null &&
                clientWebSocket.getConnection().isOpen();
    }

    public void sendMessage(String ping) {
        clientWebSocket.sendMessage(ping);
    }
}
