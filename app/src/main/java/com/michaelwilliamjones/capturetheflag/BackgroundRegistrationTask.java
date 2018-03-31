package com.michaelwilliamjones.capturetheflag;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import android.webkit.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by mikejones on 3/31/18.
 */

class BackgroundRegistrationTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "BackgroundRegTask";
    private Context context;

    public BackgroundRegistrationTask (Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... accessTokens) {
        String accessToken = accessTokens[0];

        Integer success = 0;
        if(accessToken != null && accessToken.length() > 0) {
            success = submitRegistrationToServer(accessToken);
        }

        return success;
    }

    @Override
    protected void onPostExecute(Integer success) {
        super.onPostExecute(success);
        if(success == 1) {
            // do whatever you're supposed to do on success.
        }

    }

    private Integer submitRegistrationToServer(String username, String password, String email) {
        Integer responseCode = 0;

        try {
            CookieHandler.setDefault(new java.net.CookieManager());

            URL registrationUrl = new URL("http://192.168.1.73:5000/register");
            HttpURLConnection conn = (HttpURLConnection) registrationUrl.openConnection();
            conn.setRequestProperty();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);

            Log.d(TAG, "POST /register");

            responseCode = conn.getResponseCode();
            Log.d(TAG, "O --> responseCode: " + responseCode);
            String responseString = readInputStreamToString(conn);
            Log.d(TAG, "O --> content: " + responseString);
            if(responseCode == 200) {
                Log.d(TAG, "User has registered successfully");

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                CookieManager cookieManager = CookieManager.getInstance();

                for(String cookie : cookiesHeader) {
                    Log.d(TAG, "cookies from header: " + cookie);
                    cookieManager.setCookie("192.168.1.73", cookie);
                }

            } else {
                Log.d(TAG, "HTTP STATUS: " + responseCode + " Issue with registration");
            }


        } catch (Exception e) {
            Log.d(TAG, "Exception happened");
            Log.d(TAG, e.getMessage());
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