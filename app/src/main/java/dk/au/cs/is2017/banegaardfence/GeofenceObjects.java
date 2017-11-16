package dk.au.cs.is2017.banegaardfence;

import java.io.Serializable;

/**
 * Created by Frederik on 16-11-2017.
 */

public class GeofenceObjects implements Serializable {
    private String geofenceName;
    private String alert;

    public GeofenceObjects(String geofenceName, String alert) {
        this.geofenceName = geofenceName;
        this.alert = alert;
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
}
