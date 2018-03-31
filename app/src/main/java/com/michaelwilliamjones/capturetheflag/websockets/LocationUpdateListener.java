package com.michaelwilliamjones.capturetheflag.websockets;

/**
 * Created by mikejones on 3/25/18.
 */

public interface LocationUpdateListener {
    void onLocationReceived(LocationDetails locationDetails);
}
