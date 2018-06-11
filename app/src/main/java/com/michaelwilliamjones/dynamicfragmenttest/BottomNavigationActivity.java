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
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

public class BottomNavigationActivity extends FragmentActivity implements LocationListener{

    private ViewGroup fragmentContainer;
    private static final int MY_PERMISSIONS_REQUEST_VIEW_LOCATION = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private LocationManager mLocationManager;
    private final int MIN_TIME = 500;
    private final int MIN_DISTANCE = 1;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Map<String, Marker> teammateLocationMarkers;
    private boolean isFirstLocationUpdate = true;

    private boolean isMapReady() {
        return this.mGoogleMap != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        this.teammateLocationMarkers = new HashMap<String, Marker>();
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
                        ft.replace(R.id.fragment_container, HomeFragment.newInstance());
                        ft.addToBackStack(null);
                        ft.commit();
                        return true;
                    case R.id.navigation_dashboard:
                        ft.replace(R.id.fragment_container, DashboardFragment.newInstance());
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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            mGoogleMap.animateCamera(cameraUpdate);
            CameraPosition cameraPosition;
            if (isFirstLocationUpdate) {
                cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).bearing(90).tilt(40).build();
                isFirstLocationUpdate = false;
            } else {
                 cameraPosition = new CameraPosition.Builder().target(latLng).zoom(mGoogleMap.getCameraPosition().zoom).bearing(90).tilt(40).build();
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if(this.teammateLocationMarkers.get("fakeTeammate") == null) {
                this.teammateLocationMarkers.put("fakeTeammate",
                        mGoogleMap.addMarker(new MarkerOptions().position(
                                new LatLng(location.getLatitude()-.1,
                                        location.getLongitude() - .1)).title("fake")));
            } else {
                this.teammateLocationMarkers.get("fakeTeammate").setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            // mLocationManager.removeUpdates(this);
        }
    }
}
