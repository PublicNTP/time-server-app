package org.publicntp.gnssreader.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.PermissionsHelper;
import org.publicntp.gnssreader.listener.LocationHelper;
import org.publicntp.gnssreader.model.Permission;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    @Override
    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeBottomNavigation();

        if (PermissionsHelper.permissionIsGranted(this, Permission.FINE_LOCATION)) {
            LocationHelper.registerNmeaListenerAndStartGettingFixes(this);
        } else {
            PermissionsHelper.requestPermission(this, Permission.FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Permission.FINE_LOCATION.getKey()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.i("response for permissions check contains: granted");
                LocationHelper.registerNmeaListenerAndStartGettingFixes(this);
            } else {
                Timber.i("response for permissions check contains: denied");
            }
        } else {
            Timber.w("unknown permissions type");
        }
    }

    private void initializeBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.action_info:
                    selectedFragment = InfoFragment.newInstance();
                    break;
                case R.id.action_serv:
                    selectedFragment = ServerFragment.newInstance();
                    break;
                case R.id.action_satl:
                    selectedFragment = SatelliteFragment.newInstance();
                    break;
                default:
                    selectedFragment = TimeFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, selectedFragment);
            transaction.commit();
            return true;
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, TimeFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
}
