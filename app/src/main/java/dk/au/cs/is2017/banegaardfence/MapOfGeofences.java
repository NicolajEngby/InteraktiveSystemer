package dk.au.cs.is2017.banegaardfence;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapOfGeofences extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<GeofenceObjects> currentList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_of_geofences);
        context = getApplicationContext();
        currentList = new ArrayList<>();
        readFromInternalStorage();
        addMarkerForFence();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public double[] fetchLocationName(String locationName) {
        // geoCoder object with a context argument
        //context is used to access global application or activity information
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        double latitude = 0;
        double longitude = 0;
        try {
            List<Address> address = geoCoder.getFromLocationName(locationName, 1);
            latitude = address.get(0).getLatitude();
            longitude = address.get(0).getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new double[] {latitude, longitude};
    }

    public void addMarkerForFence() {
        for (GeofenceObjects geofenceObjects : currentList) {
            double[] latlon = fetchLocationName(geofenceObjects.getGeofenceName());
            int radius = geofenceObjects.getRadius();
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latlon[0], latlon[1]))
                    .title(geofenceObjects.getGeofenceName())
                    .snippet("Radius: " + radius + "m")).showInfoWindow();
            //Instantiates a new CircleOptions object +  center/radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latlon[0], latlon[1]))
                    .radius(radius)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2);
            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);
            // more operations on the circle available...
        }
    }





    private void readFromInternalStorage() {
        ArrayList<GeofenceObjects> returnlist = new ArrayList<>();
        returnlist = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("GeoFences");
            ObjectInputStream ois = new ObjectInputStream(fis);
            returnlist = (ArrayList<GeofenceObjects>) ois.readObject();
            ois.close();
            System.out.println(returnlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentList = returnlist;
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
