package com.michaelwilliamjones.capturetheflag;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by mikejones on 4/15/18.
 */

public class BackgroundLoginTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "BackgroundLoginTask";
    private Context context;
    private LoginActivity activity;

    public BackgroundLoginTask (Context context, LoginActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... userParams) {
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        return submitLoginToServer(userParams[0], userParams[1]);
    }

    @Override
    protected void onPostExecute(Integer success) {
        super.onPostExecute(success);
        if(success == 1) {
            // do whatever you're supposed to do on success.
            //Launch the MapsActivity.
            Intent intent = new Intent(activity, MessageListActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private Integer submitLoginToServer(String username, String password) {
        Integer responseCode = 0;
        HttpURLConnection conn = null;

        try {
            CookieHandler.setDefault(new java.net.CookieManager());

            URL loginUrl = new URL(Constants.SKELETOR_URI + "/" + Constants.LOGIN_ENDPOINT);
            conn = (HttpURLConnection) loginUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(5000); // 5 seconds

            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            // send some data over the wire.
            JSONObject profileData = new JSONObject();
            profileData.put("username", username);
            profileData.put("password", password);
            outputStream.write(profileData.toString().getBytes("UTF-8"));
            outputStream.close();

            Log.d(TAG, "POST /login");

            responseCode = conn.getResponseCode();
            Log.d(TAG, "O --> responseCode: " + responseCode);
            String responseString = readInputStreamToString(conn);
            Log.d(TAG, "O --> content: " + responseString);
            if(responseCode == 200) {
                Log.d(TAG, "User has logged in successfully");

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                CookieManager cookieManager = CookieManager.getInstance();

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        Log.d(TAG, "cookies from header: " + cookie);
                        cookieManager.setCookie(Constants.SKELETOR_HOST, cookie);
                    }
                }

            } else {
                Log.d(TAG, "HTTP STATUS: " + responseCode + " Issue with login");
            }


        } catch (Exception e) {
            Log.d(TAG, "Exception happened");
            Log.d(TAG, e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if(responseCode == 200) {
            return 1;
        } else {
            return -1;
        }
    }

    public static String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;

        try {
            if((connection.getResponseCode()+"").startsWith("2") || (connection.getResponseCode()+"").startsWith("3")) {
                //success 2xx, read from inputstream
                inputStream = connection.getInputStream();
            } else {
                //fail, read from errstream
                Log.e(TAG, "reading from errorStream");
                inputStream = connection.getErrorStream();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            result = stringBuilder.toString();
        }
        catch (Exception e) {
            Log.d(TAG, "Error reading InputStream");
            result = null;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    Log.d(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }

}
