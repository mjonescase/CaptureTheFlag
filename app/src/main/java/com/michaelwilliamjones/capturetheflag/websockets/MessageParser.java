package com.michaelwilliamjones.capturetheflag.websockets;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mikejones on 3/25/18.
 */

public class MessageParser {
    public static JSONObject toJSON(String text) throws JSONException{
        Log.i("WEBSOCKET", text);
        // {"type":0,"contents":{"email":"dlhart@te.com","message":"salame","mobilenumber":"2222222222","username":"dlhart"}}
        JSONObject messageJSON = null;
        return new JSONObject(new JSONObject(text).getString("contents"));
            //if (messageJSON.getString("message").startsWith("LOCATION")) {
            //    Log.i("WEBSOCKET", "made it to the location part");
                // put a blip on the map, using the coordinates.
                // EX: work, lat42, long-82 is pretty close.
    }
}
