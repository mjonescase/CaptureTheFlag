package com.michaelwilliamjones.dynamicfragmenttest.websockets;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by mikejones on 3/11/18.
 */

public class EchoWebSocketListener extends WebSocketListener {
    private ArrayList<PubSubListener> subscribers;

    public void addSubscriber(PubSubListener subscriber) {
        if (this.subscribers == null){
            this.subscribers = new ArrayList<PubSubListener>();
        }
        subscribers.add(subscriber);
    }

    public void removeSubscriber(PubSubListener subscriber) {
        subscribers.remove(subscriber);
    }

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i("WEBSOCKETS", response.toString());
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("WEBSOCKETS","Receiving : " + text);
        // turn the message into a json object.
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(text);
        } catch (JSONException jsonException) { return; }
        for (PubSubListener listener : subscribers) {
            try {
                listener.onMessageReceived(jsonObject);
            } catch (Exception exc) {
                Log.w("TAG", "Problem handling ws message: " + exc.getMessage());
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.i("WEBSOCKETS","Receiving bytes : " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.i("WEBSOCKETS", "Closing : " + code + " / " + reason);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e("WEBSOCKETS", "Error : " + t.getMessage());
    }
}