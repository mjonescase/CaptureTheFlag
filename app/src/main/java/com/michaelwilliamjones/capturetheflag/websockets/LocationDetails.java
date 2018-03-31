package com.michaelwilliamjones.capturetheflag.websockets;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mikejones on 3/25/18.
 */

public class LocationDetails {
    private LatLng latLng;
    private String userDisplayInfo;

    public LocationDetails(double latitude, double longitude, String userDisplayInfo) {
        this.latLng = new LatLng(latitude, longitude);
        this.userDisplayInfo = userDisplayInfo;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public String getUserDisplayInfo() {
        return this.userDisplayInfo;
    }
}
