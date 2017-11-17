package dk.au.cs.is2017.banegaardfence;

import java.io.Serializable;

/**
 * Created by Frederik on 16-11-2017.
 */

public class GeofenceObjects implements Serializable {
    private String geofenceName;
    private String alert;
    private int radius;

    public GeofenceObjects(String geofenceName, String alert, int radius) {
        this.geofenceName = geofenceName;
        this.alert = alert;
        this.radius = radius;
    }

    public String getGeofenceName() {
        return geofenceName;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String newAlert) {
        alert = newAlert;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
