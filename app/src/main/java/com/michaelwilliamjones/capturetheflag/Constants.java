package com.michaelwilliamjones.capturetheflag;

/**
 * Created by mikejones on 3/31/18.
 */

public class Constants {
    public static final String LOGIN_ENDPOINT = "login/";
    public static final String SKELETOR_HOST = "192.168.1.73";
    public static final String SKELETOR_PORT = "5000";
    public static final String SKELETOR_PROTOCOL = "http";
    public static final String SKELETOR_URI = SKELETOR_PROTOCOL + "://" + SKELETOR_HOST + ":" + SKELETOR_PORT;
    public static final String REGISTRATION_ENDPOINT = "register/";
    public static final String WEBSOCKET_ENDPOINT = "ws?room=commBlue";
}
