package ie.nuim.cs.walkr;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    LocationManager locationManager;

    double final_lat;
    double final_lng;
    LatLng final_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton btn_reg_owner = (ImageButton) findViewById(R.id.Reg_btn);
        ImageButton btn_walk = (ImageButton) findViewById(R.id.Find_btn);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        toggleGPSUpdates(true);

        //Moving the user to the walker register page
        btn_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.USER_LOCATION=final_location;
                if(Constants.USER_LOCATION==null){
                    showAlert();
                    toggleGPSUpdates(true);
                } else {
                    toggleGPSUpdates(false);
                    Intent walk = new Intent(Login.this, BrowseActivity.class);
                    startActivity(walk);
                }
            }
        });

        //Moving the user to the owner register page
        btn_reg_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_reg_owner = new Intent(Login.this, Register_Walker.class);
                startActivity(i_reg_owner);
            }
        });
    }

    private Boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private Boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myintent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            final_lng = location.getLongitude();
            final_lat = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final_location = new LatLng(final_lat, final_lng);
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public void toggleGPSUpdates(boolean active) {
        if (active) {
            Log.e("LOGIN GPS:", "GPS UPDATES ACTIVE");
            if (!checkLocation()) {
                return;
            }
            Button toggle = (Button) findViewById(R.id.button4);
            if (Constants.USER_LOCATION != null) {
                locationManager.removeUpdates(locationListenerGPS);
            } else {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot update, Permission Denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1, 1, locationListenerGPS);
            }
        } else {
            Log.e("LOGIN GPS:", "GPS UPDATES NOT ACTIVE");
            locationManager.removeUpdates(locationListenerGPS);
        }
    }
}