package dk.au.cs.is2017.banegaardfence;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DistanceToTarget extends Activity {

    private TextView distanceToTarget;
    private LocationManager locationManager;
    private double currentLat;
    private double currentLon;
    private double newLat, newLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_to_target);

        distanceToTarget = (TextView) findViewById(R.id.distanceToTgt);
        String sLon = getIntent().getStringExtra("lon");
        newLon = Double.valueOf(sLon);
        String sLat = getIntent().getStringExtra("lat");
        newLat = Double.valueOf(sLat);
        System.out.println("new lat: " + newLat + " new lon: " + newLon);


        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        if(location != null){
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();
        }
        // missing getting the old location and the target location as Lon and Lat
        Location currentPosition = new Location("Current Position");
        currentPosition.setLatitude(currentLat);
        currentPosition.setLongitude(currentLon);

        Location target = new Location("Target");
        target.setLatitude(newLat);
        target.setLongitude(newLon);

        double distance = currentPosition.distanceTo(target);
        distanceToTarget.setText(String.valueOf(distance));

    }

}
