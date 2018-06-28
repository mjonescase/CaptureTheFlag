package com.michaelwilliamjones.dynamicfragmenttest.websockets;

/**
 * Created by mikejones on 6/28/18.
 */

public interface WebSocketConnectionListener {
    void onWebSocketConnectionSuccess();
    void onWebSocketConnectionFailure();
}
