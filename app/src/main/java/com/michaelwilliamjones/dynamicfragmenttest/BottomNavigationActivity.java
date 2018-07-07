package com.michaelwilliamjones.dynamicfragmenttest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michaelwilliamjones.dynamicfragmenttest.websockets.EchoWebSocketListener;
import com.michaelwilliamjones.dynamicfragmenttest.websockets.PubSubListener;
import com.michaelwilliamjones.dynamicfragmenttest.websockets.WebSocketConnectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class BottomNavigationActivity extends FragmentActivity implements LocationListener, PubSubListener, WebSocketConnectionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private ViewGroup fragmentContainer;
    private static final int MY_PERMISSIONS_REQUEST_VIEW_LOCATION = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private LocationManager mLocationManager;
    private final int MIN_TIME = 500;
    private final int MIN_DISTANCE = 1;
    private SupportMapFragment mMapFragment;
    private HomeFragment mHomeFragment;
    private DashboardFragment mDashboardFragment;
    private String username = "";
    private String hostname = "";
    private GoogleMap mGoogleMap;
    private Map<String, Marker> teammateLocationMarkers;
    private boolean isFirstLocationUpdate = true;
    private WebSocket mWebSocket;
    private static final String HOSTNAME = "capturetheflag-env.bfkik8h6hw.us-east-1.elasticbeanstalk.com";

    private boolean isMapReady() {
        return this.mGoogleMap != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        this.teammateLocationMarkers = new HashMap<String, Marker>();
        mHomeFragment = HomeFragment.newInstance();
        mDashboardFragment = DashboardFragment.newInstance();
        final BottomNavigationActivity that = this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_VIEW_LOCATION);
        }

         this.mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        ft.replace(R.id.fragment_container, mHomeFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        return true;
                    case R.id.navigation_dashboard:

                        ft.replace(R.id.fragment_container, mDashboardFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        return true;
                    case R.id.navigation_notifications:
                        hideSoftKeyboard();
                        if (!isMapReady()) {
                            mMapFragment = SupportMapFragment.newInstance();
                            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    mGoogleMap = googleMap;
                                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        mGoogleMap.setMyLocationEnabled(true);
                                    } else {
                                        Log.w("tag", "location access not enabled");
                                    }
                                }
                            });
                        }

                        ft.replace(R.id.fragment_container, mMapFragment);
                        ft.addToBackStack(null);
                        ft.commit();

                        return true;
                }

                return false;
            }
        };


        setContentView(R.layout.activity_bottom_navigation);

        fragmentContainer = (ViewGroup) findViewById(R.id.fragment_container);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // choose the settings screen at startup.
        mOnNavigationItemSelectedListener.onNavigationItemSelected(navigation.getMenu().getItem(0));
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        switch( code ){
            case MY_PERMISSIONS_REQUEST_VIEW_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                }
                break;
            default:
                break;
        }
    }

    public void handleAccessFineLocationAllowed() {
    }

    // LocationListener interface implementations
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onLocationChanged(Location location) {
        if(this.mGoogleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(mWebSocket != null && username != null){
                JSONObject websocketMessage = new JSONObject();
                try {
                    websocketMessage.put("username", username);
                    websocketMessage.put("latitude", location.getLatitude());
                    websocketMessage.put("longitude", location.getLongitude());
                    websocketMessage.put("message", "");
                    String toSend = websocketMessage.toString();
                    mWebSocket.send(toSend);
                } catch (JSONException jsonException) {}
            }
            /* CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            mGoogleMap.animateCamera(cameraUpdate);
            CameraPosition cameraPosition;
            if (isFirstLocationUpdate) {
                cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).bearing(90).tilt(40).build();
                isFirstLocationUpdate = false;
            } else {
                 cameraPosition = new CameraPosition.Builder().target(latLng).zoom(mGoogleMap.getCameraPosition().zoom).bearing(90).tilt(40).build();
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // mLocationManager.removeUpdates(this); */
        }
    }


    public void onConnectClick(View view){
        // get the username text
        username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        String passcode = ((EditText) findViewById(R.id.passcode)).getText().toString(); //"red8648172";

        // do websocket stuff.
        if(mWebSocket != null) {
            mWebSocket.close(1000, "Normal Closure");
        }

        OkHttpClient webSocketClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://" + HOSTNAME + "/" + "ws?room=" + passcode).build();
        HttpUrl url = request.url();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        listener.addSubscriber(this);
        listener.addConnectionListener(this);
        mWebSocket = webSocketClient.newWebSocket(request, listener);
        webSocketClient.dispatcher().executorService().shutdown();
    }


    public void onMessageReceived(JSONObject message) {
        try {
            final String username = message.getString("username");
            final double latitude = message.getDouble("latitude");
            final double longitude = message.getDouble("longitude");
            final String messageContent = message.getString("Message");


            if (message.getString("username") != this.username && mGoogleMap != null && messageContent.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add a marker for this user if it's not there already.
                        if(teammateLocationMarkers.get(username) == null) {
                            teammateLocationMarkers.put(username,
                                    mGoogleMap.addMarker(new MarkerOptions().position(
                                            new LatLng(latitude,
                                                    longitude)).title("fake")));
                        } else {
                            teammateLocationMarkers.get(username).setPosition(new LatLng(latitude, longitude));
                        }
                    }
                });

            } else if (!messageContent.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    mDashboardFragment.addNewMessage(username + ": " + messageContent);
                    }

                });
            }
        } catch (JSONException jsonException) {
            Log.w("messagereceived", "there was a problem decoding the json");
        }
    }

    public void onSendClick(View view) {
        EditText chatTextField = findViewById(R.id.messageContent);
        String messageText = chatTextField.getText().toString();
        chatTextField.setText("");
        hideSoftKeyboard();
        if(mWebSocket != null && username != null){
            JSONObject websocketMessage = new JSONObject();
            try {
                websocketMessage.put("username", username);
                websocketMessage.put("latitude", 0.0);
                websocketMessage.put("longitude", 0.0);
                websocketMessage.put("message", messageText);
                String toSend = websocketMessage.toString();
                mWebSocket.send(toSend);
            } catch (JSONException jsonException) {

            }
        }
    }

    public void onWebSocketConnectionSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    new WebsocketConnectionAlertDialogFragment().show(ft, "TAG");
                    hideSoftKeyboard();
                } catch (Exception exc) {
                    Log.w("TAG", "Problem showing modal dialog: " + exc.getMessage());
                }
            }
        });
    }

    public void onWebSocketConnectionFailure() {
        // show an error alert dialog
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    new WebsocketConnectionFailureDialogFragment().show(ft, "TAG");
                } catch (Exception exc) {
                    Log.w("TAG", "Problem showing modal dialog: " + exc.getMessage());
                }
            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
