package dk.au.cs.is2017.banegaardfence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Geofence;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "IT2-g08";
    public static final int LOCATION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private Geofence mBanegaardFence;
    private GeofencingRequest mRequest;
    private PendingIntent mPi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
        // Let's create a Geofence around the Hovedbanegård
        mBanegaardFence = new Geofence.Builder()
                .setRequestId("hovedbanegaard")
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(56.1503116, 10.2047365, 1500)
                .build();

        mRequest = new GeofencingRequest.Builder()
                .addGeofence(mBanegaardFence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        createAlert(alert);
    }

    public void createAlert(String alert) {
        // Create an Intent pointing to the IntentService
        Intent intent = new Intent(this,
                ReceiveGeoFenceTransitionService.class);
        intent.putExtra("alertString", alert);
        mPi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mRequest, mPi);
            Log.d(TAG, "We added the geofence!");
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Play Services connection suspended!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google Play Services connection failed!");
    }
}
