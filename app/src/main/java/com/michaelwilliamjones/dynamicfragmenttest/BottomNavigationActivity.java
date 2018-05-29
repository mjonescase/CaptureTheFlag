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

public class BottomNavigationActivity extends FragmentActivity implements LocationListener{

    private ViewGroup fragmentContainer;
    private static final int MY_PERMISSIONS_REQUEST_VIEW_LOCATION = 1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private LocationManager mLocationManager;
    private final int MIN_TIME = 5;
    private final int MIN_DISTANCE = 1;
    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        final LocationListener locationListener = this;
        final BottomNavigationActivity that = this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
                        final SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mGoogleMap = googleMap;
                                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
                                    mGoogleMap.setMyLocationEnabled(true);
                                } else {
                                    Log.w("tag", "location access not enabled");
                                }
                            }
                        });

                        ft.replace(R.id.fragment_container, mapFragment);
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mGoogleMap.animateCamera(cameraUpdate);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(10).bearing(90).tilt(40).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mLocationManager.removeUpdates(this);
    }
}
