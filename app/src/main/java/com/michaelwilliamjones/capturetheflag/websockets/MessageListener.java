package com.michaelwilliamjones.capturetheflag.websockets;

/**
 * Created by mikejones on 3/14/18.
 */

public interface MessageListener {
    void onMessageReceived(String text);
}
