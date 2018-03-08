package com.michaelwilliamjones.capturetheflag.dataAccessObjects;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.michaelwilliamjones.capturetheflag.beans.FacebookProfileBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mikejones on 2/11/18.
 */

public class FacebookProfileDAO {
    private AccessToken accessToken;
    private FacebookProfileBean myProfile;

    public FacebookProfileDAO(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Reach out to Facebook API. Get this user's profile, marshal it and return it.
     *
     * Parameters: None
     *
     * Returns: this user's profile.
     *
     * Rasies: None
     */
    public GraphRequest fetchMyProfile() {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            myProfile = FacebookProfileBean.fromJSON(object);
                        } catch(JSONException jsonException) {
                            // TODO handle this somehow
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
        return request;
    }
}
