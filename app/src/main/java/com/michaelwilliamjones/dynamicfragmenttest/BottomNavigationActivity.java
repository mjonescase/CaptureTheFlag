package com.michaelwilliamjones.dynamicfragmenttest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

public class BottomNavigationActivity extends FragmentActivity {

    private ViewGroup fragmentContainer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // fragmentContainer.setText(R.string.title_home);
                    // inflate a simple fragment.
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
                    ft.replace(R.id.fragment_container, LiveMapFragment.newInstance());
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        fragmentContainer = (ViewGroup) findViewById(R.id.fragment_container);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
