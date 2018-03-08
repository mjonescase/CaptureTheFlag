package com.michaelwilliamjones.capturetheflag.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mikejones on 2/11/18.
 */

public class FacebookProfileBean implements Serializable {
    private String id;
    private String name;

    private FacebookProfileBean() { }

    public String getID() {
        return this.id;
    }

    private void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public static FacebookProfileBean fromJSON(JSONObject jsonObject) throws JSONException {
        FacebookProfileBean bean = new FacebookProfileBean();
        bean.setID(jsonObject.getString("id"));
        bean.setName(jsonObject.getString("name"));
        return bean;
    }
}
