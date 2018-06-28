package com.michaelwilliamjones.dynamicfragmenttest;

import android.Manifest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class BottomNavigationActivity extends FragmentActivity implements LocationListener, PubSubListener {

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_VIEW_LOCATION);
        }
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
        hostname = ((EditText) findViewById(R.id.hostname_input)).getText().toString();

        // do websocket stuff.
        if(mWebSocket != null) {
            mWebSocket.close(1000, "Normal Closure");
        }

        OkHttpClient webSocketClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://" + hostname + "/" + "ws?room=commBlue").build();
        HttpUrl url = request.url();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        listener.addSubscriber(this);
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
        // get the username text
        String messageText = ((EditText) findViewById(R.id.messageContent)).getText().toString();
        if(mWebSocket != null && username != null){
            JSONObject websocketMessage = new JSONObject();
            try {
                websocketMessage.put("username", username);
                websocketMessage.put("latitude", 0.0);
                websocketMessage.put("longitude", 0.0);
                websocketMessage.put("message", messageText);
                String toSend = websocketMessage.toString();
                mWebSocket.send(toSend);
            } catch (JSONException jsonException) {}
        }
    }
}
