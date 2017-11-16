package dk.au.cs.is2017.banegaardfence;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DistanceToTarget extends Activity {

    private TextView distanceText;
    private LocationManager locationManager;
    private Double newLat;
    private Double newLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_to_target);

        distanceText = (TextView) findViewById(R.id.distanceToTgt);

        String newLon1 = (String) getIntent().getStringExtra("lon");
        System.out.println(newLon1);
        if (getIntent().getStringExtra("lon") != null && getIntent().getStringExtra("lat") != null) {
            newLon = Double.valueOf(getIntent().getStringExtra("lon"));
            newLat = Double.valueOf(getIntent().getStringExtra("lat"));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //kan man få location på emu=?

        Location currentLocation = new Location("Current Position");
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setLatitude(location.getLatitude());
        Location targetLocation = new Location("Target");
        targetLocation.setLatitude(newLat);
        targetLocation.setLongitude(newLon);
        Float distance = currentLocation.distanceTo(targetLocation);
        distanceText.setText(distance.toString());
    }
}