package com.michaelwilliamjones.capturetheflag.websockets;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by mikejones on 3/11/18.
 */

public class EchoWebSocketListener extends WebSocketListener {

    private ArrayList<LocationUpdateListener> locationUpdateListeners;
    private ArrayList<MessageListener> messageListeners;

    public EchoWebSocketListener() {
        super();
        this.locationUpdateListeners = new ArrayList<LocationUpdateListener>();
        this.messageListeners = new ArrayList<MessageListener>();
    }

    public void addMessageListener(MessageListener listener) {
        this.messageListeners.add(listener);
    }

    public void addLocationListener(LocationUpdateListener listener) {
        this.locationUpdateListeners.add(listener);
    }

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i("WEBSOCKETS", response.toString());
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.i("WEBSOCKETS","Receiving : " + text);
        JSONObject messageJSON = null;
        try {
            messageJSON = MessageParser.toJSON(text);
            // see if it's a location type message.
            String messageString = messageJSON.getString("message");
            if ( messageString.startsWith("LOCATION") ) {
                // parse the string and get the lat and lng out of it.
                String [] latLngStrings = messageString.split("LOCATION: ")[0].split(", ");
                LocationDetails locationDetails = new LocationDetails(Double.parseDouble(latLngStrings[0]), Double.parseDouble(latLngStrings[1]), messageJSON.getString("email"));
                // send it to all the location listeners.
                for (LocationUpdateListener listener: this.locationUpdateListeners) {
                    listener.onLocationReceived(locationDetails);
                }
            }
        } catch (JSONException jsonException) {
            Log.e("WEBSOCKETS", "Malformed message data; expected JSON. Ignoring.");
        } finally {
            if ( messageJSON == null ) {
                return;
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
