package com.michaelwilliamjones.dynamicfragmenttest.websockets;

import org.json.JSONObject;

/**
 * Created by mikejones on 6/10/18.
 */

public interface PubSubListener {
    void onMessageReceived(JSONObject message);
}
