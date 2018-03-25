package ie.nuim.cs.walkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.List;

/**
 * Created by fionn on 21/12/2017.
 *
 *   HEAVY NOTE:
 *      THIS METHOD IS NOT USED IN AN XML VIEW BUT IT IS REFERENCED BY OTHER METHODS FOR FUNCTIONALITY
 *      DO NOT REMOVE!
 */

public class CustomAdapter extends ArrayAdapter<Dog>{
    public CustomAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CustomAdapter(Context context, int resource, List<Dog> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_each_item, null);
        }

        Dog p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.textName);
            TextView tt2 = (TextView) v.findViewById(R.id.textDistance);
            TextView tt3 = (TextView) v.findViewById(R.id.textBreed);

            if (tt1 != null) {
                tt1.setText(p.getName());
            }

            if (tt2 != null) {
                tt2.setText(showDist(p.getLocation(), Constants.USER_LOCATION));
            }

            if (tt3 != null) {
                tt3.setText(p.getBreed());
            }
        }

        return v;
    }

    public String showDist(LatLng from, LatLng to){
        //Getting distance
        double distance= SphericalUtil.computeDistanceBetween(from, to);
        //Log.e(Constants.TAG, "DISTANCE BETWEEN TWO MARKERS IS COMPUTED AT: " + distance);
        String strDist;
        if(distance <=1000.0){
            strDist = String.format("%4.2f%s", distance, "m");
        } else {
            distance = distance/1000;
            strDist = String.format("%4.2f%s", distance, "km");
        }

        return strDist;
    }
}
