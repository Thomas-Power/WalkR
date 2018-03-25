package ie.nuim.cs.walkr;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng profLocation;
    Dog profDog = new Dog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final TextView tv_name = findViewById(R.id.textView5);
        final TextView tv_age = findViewById(R.id.textView6);
        final TextView tv_bio = findViewById(R.id.textView7);
        final TextView tv_phone = findViewById(R.id.textView8);
        final TextView tv_email = findViewById(R.id.textView9);

        //Calling Database? Yes, we need the Database.
        String targetID = getIntent().getStringExtra("TargetDog");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+targetID);


// Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once

                Log.e(Constants.TAG, "LOOKING AT THE DATABASE");

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().equals("location")) {
                        double latitude = child.child("latitude").getValue(Double.class);
                        double longitude = child.child("longitude").getValue(Double.class);
                        profLocation = new LatLng(latitude, longitude);
                        profDog.setLocation(profLocation);
                    } else if (child.getKey().equals("name")) {
                        profDog.setName(child.getValue(String.class));
                    } else if (child.getKey().equals("age")) {
                        profDog.setAge(child.getValue(Integer.class));
                    } else if (child.getKey().equals("breed")) {
                        profDog.setBreed(child.getValue(String.class));
                    } else if (child.getKey().equals("bio")) {
                        profDog.setBio(child.getValue(String.class));
                    } else {
                        Log.e(Constants.TAG, "ERROR IN READING/ASSIGNING TO OBJECT");
                    }

                }


                // set up the markers
                setMarkers(Constants.USER_LOCATION, profLocation);

                //Update the boxes
                tv_name.setText(profDog.getName());
                tv_age.setText(profDog.getAge()+"");
                tv_bio.setText(profDog.getBio());
                tv_phone.setText(profDog.getLocation().toString());
                tv_email.setText(profDog.getBreed());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(Constants.TAG, "Failed to read value.", error.toException());
            }
        });



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setMarkers(LatLng from, LatLng to){
        // Add a marker in Dublin and move the camera
        // In practise the LatLng will be read from the GPS location of the user's profile data
        mMap.addMarker(new MarkerOptions()
                .position(from));
        mMap.addMarker(new MarkerOptions()
                .position(to)
                .title(profDog.getName())
                .snippet(showDist(from, to)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(to));
        mMap.setMaxZoomPreference(17.0f);
        mMap.setMinZoomPreference(12.0f);
    }

    public String showDist(LatLng from, LatLng to){
        //Getting distance
        double distance=SphericalUtil.computeDistanceBetween(from, to);
        //Log.e(Constants.TAG, "DISTANCE BETWEEN TWO MARKERS IS COMPUTED AT: " + distance);
        String strDist;
        if(distance <=1000.0){
            strDist = String.format("%4.2f%s", distance, "m");
        } else {
            distance = distance/1000;
            strDist = String.format("%4.2f%s", distance, "km");
        }

        strDist = "The markers are " + strDist + " apart";

        mMap.addPolyline(new PolylineOptions()
                .add(from, to)
                .width(5)
                .color(Color.argb(170, 255, 0, 0))
                .startCap(new RoundCap())
                .endCap(new RoundCap()));

        return strDist;
    }
}
