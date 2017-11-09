package dk.au.cs.is2017.banegaardfence;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Geofence;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "IT2-g08";
    public static final int LOCATION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private Geofence mBanegaardFence;
    private GeofencingRequest mRequest;
    private PendingIntent mPi;
    private Context context;
    private Button addAlert;
    private EditText address;
    private EditText alertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        // Set up Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        addAlert = (Button)findViewById(R.id.addAlert);
        address   = (EditText)findViewById(R.id.address);
        alertText   = (EditText)findViewById(R.id.alert);

        addAlert.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        double[] array = fetchLocationName(address.getText().toString());
                        double lat = array[0];
                        double lon = array[1];
                        System.out.println(array[0] + " :lat  +    lon: " + array[1]);
                        createAlert(alertText.getText().toString(), lat, lon);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Play Services connected!");
        createGeoFence("this is an alert");
    }

    public void createGeoFence(String alert) {
        double lat = 56.1503116;
        double lon = 10.2047365;

        // Let's create a Geofence around the Hovedbanegård
        mBanegaardFence = new Geofence.Builder()
                .setRequestId("hovedbanegaard")
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(lat, lon, 1500)
                .build();

        mRequest = new GeofencingRequest.Builder()
                .addGeofence(mBanegaardFence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        createAlert(alert, lat, lon);
    }

    public void createAlert(String alert, double lat, double lon) {
        // Create an Intent pointing to the IntentService
        Intent intent = new Intent(this,
                ReceiveGeoFenceTransitionService.class);
        intent.putExtra("alertString", alert);
        intent.putExtra("locationName", alertText.getText().toString());
        mPi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mRequest, mPi);
            Log.d(TAG, "We added the geofence!");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String message = "Location permission accepted. Geofence will be created.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    // OK, request it now
                    LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mRequest, mPi);
                    Log.d(TAG, "We added the geofence!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    String message = "Location permission denied. Geofence will not work.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public double[] fetchLocationName(String locationName) {
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

    @Override
    public void onConnectionSuspended ( int i){
        Log.d(TAG, "Google Play Services connection suspended!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult){
        Log.d(TAG, "Google Play Services connection failed!");
    }
}
