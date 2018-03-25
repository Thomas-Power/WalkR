package ie.nuim.cs.walkr;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Comparator;

/**
 * Created by fionn on 15/12/2017.
 *   HEAVY NOTE:
 *      THIS METHOD IS NOT USED IN AN XML VIEW BUT IT IS REFERENCED BY OTHER METHODS FOR FUNCTIONALITY
 *      DO NOT REMOVE!
 */

public class Dog implements Comparable {
    private String userID;
    private String name;
    private int age;
    private String breed;
    private String bio;
    private LatLng location;
    //This information is planned but as of yet not included in the xml layout
    // when it is added this will need un commenting and adding to the constructors (the Dog() methods)
    // Will also need getter/setter methods like those below (note: a methods NEEDS "()" even if there is no parameters being passed)
    // Multiple areas will need updating too but where will be obvious when you try to run it and the errors pop up
    //private String phone;

    public Dog(){
        String userID = new String();
        name = "Beethoven";
        age = 42;
        breed = "Collie";
        bio = "Default Constructor: This Dog is a Fake Dog (or is it?)! \nAnd it lives in Maynooth!";
        location = new LatLng(53.383828, -6.600851);
    }

    public Dog(String userID, String dogName, int dogAge, String dogBreed, String bio, LatLng location){
        this.userID = userID;
        this.name = dogName;
        this.age = dogAge;
        this.breed = dogBreed;
        this.bio = null;
        this.location = location;
    }

    public void setUserID(String userID) {
        this.userID=userID;
    }
    public String getUserID() {
        return userID;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setAge(int age){
        this.age = age;
    }
    public int getAge(){
        return age;
    }

    public void setBio(String bio){
        this.bio = bio;
    }
    public String getBio(){
        return bio;
    }

    public LatLng getLocation(){
        return location;
    }
    public double getLocationLatidude() { return location.latitude; }
    public double getLocationLongitude() { return location.longitude; }
    public void setLocation(LatLng location){
        this.location = location;
    }
    public void setLocationLatitude(Double latitude) {
        double lat = latitude;
        double lng = this.location.longitude;
        location = new LatLng(lat, lng);
    }
    public void setLocationLongitude (Double newLng) {
        double lat = this.location.latitude;
        double lng = newLng;
        location = new LatLng(lat, lng);
    }

    public String getBreed(){ return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    @Override
    public int compareTo(@NonNull Object o) {
        Dog compareDog = (Dog) o;
        LatLng location = compareDog.getLocation();
        int compareDistance = (int) SphericalUtil.computeDistanceBetween(Constants.USER_LOCATION, location);
        int thisDistance = (int) SphericalUtil.computeDistanceBetween(Constants.USER_LOCATION, this.location);
        return thisDistance-compareDistance;
    }
}
