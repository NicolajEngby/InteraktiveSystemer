package dk.au.cs.is2017.banegaardfence;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;

import android.app.PendingIntent;
import android.net.Uri;
import android.util.Log;
import android.graphics.BitmapFactory;

import android.os.Build;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.NotificationChannel;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ReceiveGeoFenceTransitionService extends IntentService {

    // Notification channel ID, needed for API 26 and higher
    public final static String CHANNEL_ID = "default";

    public ReceiveGeoFenceTransitionService() {
        super("ReceiveGeoFenceTransitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        Context context = getApplicationContext();
        String alert = (String) intent.getExtras().get("alertString");
        String locationName = (String) intent.getExtras().get("locationName");
        if (event.hasError()) {
            // TODO: Handle error
        } else {
            int transition = event.getGeofenceTransition();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                String transitionType = getTransitionString(transition);

                Log.d(MainActivity.TAG, getString(R.string.geofence_transition_notification_title, transitionType));

                // Send a notification, when clicked, open website
                Intent notificationIntent = new Intent(this, DistanceToTarget.class);
                System.out.println(notificationIntent + "send");

                PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);

                // Create a notification channel if on API 26 (Android O) and above
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Default Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                    // Configure the notification channel.
                    notificationChannel.setDescription("Channel description");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                Notification notification = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID)
                        .setContentTitle(locationName)
                        .setContentText(alert)
                        .setTicker("ticker")
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.stat_sys_gps_on)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.rejse))
                        .build();

                Log.d(MainActivity.TAG, "Notification created");

                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                manager.notify(1, notification);

                Log.d(MainActivity.TAG, "Notified!");
            } else {
                // TODO: Handle invalid transition
            }
        }
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in GeofenceObjects
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
}
