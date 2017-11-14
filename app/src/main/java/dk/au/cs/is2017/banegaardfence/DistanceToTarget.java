package dk.au.cs.is2017.banegaardfence;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DistanceToTarget extends Activity {

    MainActivity mainActivity;
    private TextView distanceToTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_to_target);

        distanceToTarget = (TextView) findViewById(R.id.distanceToTgt);
        double array[] = mainActivity.returnArray();

        // missing getting the old location and the target location as Lon and Lat
        /* Location currentPosition = new Location("Current Position");
        oldLat = ;
        oldLon =;

        Location target = new Location("Target");
        oldLat = array[0];
        oldLon = array[1];


        double distance = currentPosition.distanceTo(target);
        distanceToTarget.setText(String.valueOf(distance));


    }

}
