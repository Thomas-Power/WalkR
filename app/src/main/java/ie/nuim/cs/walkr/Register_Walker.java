package ie.nuim.cs.walkr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Walker extends AppCompatActivity {

    LocationManager locationManager;
    LatLng final_location;
    Double final_lat;
    Double final_lng;
    String dogName;
    Integer dogAge;
    String dogBreed;
    String dogBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister_walker);

        Button btn_submit = findViewById(R.id.button2);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Letting the user Login [just moving them for now]
        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                EditText et_name = findViewById(R.id.editText);
                EditText et_age = findViewById(R.id.editText2);
                EditText et_bio = findViewById(R.id.editText3);
                Spinner sp_breed = findViewById(R.id.spinner);

                //TODO: Replace Breed information with user input
                dogName = et_name.getText().toString();
                dogBio = et_bio.getText().toString();
                dogBreed = "none";
                //When someone sets up the spinner with dog breeds you can un comment this and delete the other value assignment
//                dogBreed = sp_breed.getSelectedItem().toString();
                dogAge = Integer.parseInt(et_age.getText().toString());


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                Constants.currentUserID = myRef.push().getKey();
                Dog current = new Dog(Constants.currentUserID, dogName, dogAge, dogBreed, dogBio, final_location);
                myRef.child("Users").child(Constants.currentUserID).setValue(current);

                if(Constants.USER_LOCATION!=null) {
                    toggleGPSUpdates();
                    Intent i_login = new Intent(Register_Walker.this, BrowseActivity.class);
                    startActivity(i_login);
                } else {
                    showAlert();
                }
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

    public void toggleGPSUpdates(View view) {
        if (!checkLocation()) {
            return;
        }
        Button toggle = (Button) findViewById(R.id.button4);
        if (toggle.getText().equals("Yes GPS")) {
            locationManager.removeUpdates(locationListenerGPS);
            toggle.setText("No GPS");
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Cannot update, Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1, 1, locationListenerGPS);
            toggle.setText("Yes GPS");
        }
    }
    public void toggleGPSUpdates(){
        locationManager.removeUpdates(locationListenerGPS);
    }
}
