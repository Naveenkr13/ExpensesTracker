package com.asndeveloper.expensestracker;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.asndeveloper.expensestracker.Fragment.HistoryFragment;
import com.asndeveloper.expensestracker.Fragment.HomeFragment;
import com.asndeveloper.expensestracker.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_nav);
        // load HomeFragment only once (when app launches)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new HomeFragment())
                    .commit();
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment=null;
                if (menuItem.getItemId() == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (menuItem.getItemId() == R.id.nav_hist) {
                    fragment = new HistoryFragment();
                } else if (menuItem.getItemId() == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main, fragment)
                        .commit();

                return true;
            }
        });
        askSmsPermissions();

    }
    private void askSmsPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_PHONE_STATE
                },
                100);
    }

}