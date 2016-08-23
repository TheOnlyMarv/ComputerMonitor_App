package de.theonlymarv.computermonitor;


import android.app.Activity;
import android.util.Log;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import de.theonlymarv.computermonitor.Interfaces.WebSocketEvents;
import de.theonlymarv.computermonitor.RemoteClasses.WebSocket.Action;
import de.theonlymarv.computermonitor.RemoteClasses.WebSocket.RemoteAction;
import de.theonlymarv.computermonitor.RemoteClasses.WebSocket.RemoteResponse;

/**
 * Created by Marvin on 14.08.2016.
 */
public class WebSocket {
    private final WebSocketEvents webSocketEvents;
    private WebSocketClient mWebSocketClient;
    private String url;
    private Activity activity;
    private boolean connected;

    public WebSocket(Activity activity, WebSocketEvents webSocketEvents, String url){
        this.activity = activity;
        this.webSocketEvents = webSocketEvents;
        this.url = url;
        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webSocketEvents.onOpened();
                    }
                });
                sendMessage(Action.Information, 0);
                connected = true;
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        decodeMessage(message);
                        Log.i("Websocket", "Message " + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webSocketEvents.onClosed();
                    }
                });
                connected = false;
            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = e.getMessage();
                        if (e instanceof java.net.ConnectException){
                            message = "Connection failed: Time out";
                        }
                        webSocketEvents.onError(message);
                    }
                });
            }
        };
        mWebSocketClient.connect();
    }

    private void decodeMessage(String message){
        Genson genson = new Genson();
        try {
            final RemoteResponse remoteResponse = genson.deserialize(message, RemoteResponse.class);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   webSocketEvents.onMessage(remoteResponse);
                }
            });
        }
        catch (JsonBindingException jbe){
            Log.i("Decode", "decodeMessage: " + jbe.toString());
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendMessage(Action action, int value) {
        Genson genson = new Genson();
        mWebSocketClient.send(genson.serialize(new RemoteAction(action, value)));
    }

    public void closeConnection(){
        mWebSocketClient.close();
    }

    public String getUrl() {
        return url;
    }
}
