package ie.nuim.cs.walkr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;

public class BrowseActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView dogList;
    private final String TAG = "BrowseActivity";

    //I'll be honest, I wrote this in the hopes of being able to make a profile edit + Pedometer page but I ran out of time
    // For now these buttons will crash the app if you press them because its looking for an Intent to switch to and not finding one
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        dogList=(ListView) findViewById(R.id.listView);

        populateListView();
    }

    private void populateListView(){
        Log.e(TAG, "populateListView: Displaying data in the ListView.");
        //Calling Database? Yes, we need the Database.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        //get data and append to a list
        final ArrayList<Dog> listData = new ArrayList<>();
        final String IDs[] = new String[100];
// Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once

                Log.e(TAG, "LOOKING AT THE DATABASE");
                Constants.numDog=0;
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    //For every user in the database
                    //Get their userID and get the values we need from that entry and add them to the list for display
                    IDs[Constants.numDog] = child.getKey();
                    Log.e(TAG, "ADDING ID TO ID ARRAY: "+IDs[Constants.numDog]);
                    Constants.numDog++;
                }

                //GOING A LAYER DEEPER IN THE DATABASE
                for(int i=0; i<Constants.numDog; i++) {
                    Log.e(TAG, "ADDING TO THE LISTVIEW BEGINS");

                    DatabaseReference newRef = database.getReference("Users/" + IDs[i]);
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Dog profDog = new Dog();
                            Log.e(Constants.TAG, "LOOKING AT THE DATABASE");

                            for(DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.e(TAG, "LOOKING FOR DOG INFORMATION");
                                if (child.getKey().equals("location")) {
                                    double latitude = child.child("latitude").getValue(Double.class);
                                    double longitude = child.child("longitude").getValue(Double.class);
                                    LatLng profLocation = new LatLng(latitude, longitude);
                                    profDog.setLocation(profLocation);
                                } else if (child.getKey().equals("name")) {
                                    profDog.setName(child.getValue(String.class));
                                    Log.e(TAG, "FOUND "+profDog.getName());
                                } else if (child.getKey().equals("breed")) {
                                    profDog.setBreed(child.getValue(String.class));
                                } else if (child.getKey().equals("userID")){
                                    profDog.setUserID(child.getValue(String.class));
                                }
                            }
                            Log.e(TAG, "ADDING CURRENT DOG: "+profDog.getName());
                            listData.add(profDog);
                            Constants.doneYet++;
                            if(Constants.doneYet==Constants.numDog){
                                populateListViewEntries(listData);
                                    Log.e(TAG, "BUILDING THE LIST, ALL ABOARD!!!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(Constants.TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void populateListViewEntries(ArrayList<Dog> listData){
        Collections.sort(listData);
        CustomAdapter adapter = new CustomAdapter(BrowseActivity.this, R.layout.listview_each_item, listData);
        dogList.setAdapter(adapter);

        //Set and onItemClickListener
        dogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dog selecDog = (Dog) parent.getItemAtPosition(position);

                Intent i_profile = new Intent(BrowseActivity.this, MapActivity.class);
                i_profile.putExtra("TargetDog", selecDog.getUserID());
                startActivity(i_profile);
            }
        });
    }
}
