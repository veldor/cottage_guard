package net.veldor.cottage_guard.socket;


import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import net.veldor.cottage_guard.App;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ClientWebSocket {

    private static final String TAG = "surprise";
    private final MessageListener listener;
    private final String host;
    private WebSocket ws;


    public ClientWebSocket(MessageListener listener, String host) {
        this.listener = listener;
        this.host = host;
    }

    public void connect() {
        new Thread(() -> {
            if (ws != null) {
                reconnect();
            } else {
                try {
                    WebSocketFactory factory = new WebSocketFactory();
                    ws = factory.createSocket(host);
                    ws.addListener(new SocketListener());
                    ws.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void reconnect() {
        try {
            ws = ws.recreate().connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebSocket getConnection() {
        return ws;
    }

    public void close() {
        ws.disconnect();
    }

    public void sendMessage(String ping) {
        ws.sendText(ping);
    }


    public class SocketListener extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.i(TAG, "onConnected");
        }

        public void onTextMessage(WebSocket websocket, String message) {
            listener.onSocketMessage(message);
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());
            App.getInstance().mLiveSocketConnected.postValue(false);
            reconnect();
        }

        @Override
        public void onDisconnected(WebSocket websocket,
                                   WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                   boolean closedByServer) {
            Log.i(TAG, "onDisconnected");
            if (closedByServer) {
                App.getInstance().mLiveSocketConnected.postValue(false);
                reconnect();
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());
            App.getInstance().mLiveSocketConnected.postValue(false);
            reconnect();
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            websocket.sendPing("Are you there?");
        }
    }

    public interface MessageListener {
        void onSocketMessage(String message);
    }
}
