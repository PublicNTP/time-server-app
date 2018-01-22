package org.publicntp.gnssreader.model;

import android.location.GnssClock;
import android.location.GnssMeasurement;

import java.util.Collection;

public class TimeAndLocation {

    private String timeAccuracy;
    private String timeDisplay;
    private String timeUnits;
    private String timeZone;

    //private final GnssClock gnssClock;

    private TimeAndLocation(Collection<GnssMeasurement> measurements) {

        for (GnssMeasurement measurement : measurements) {
            measurement.describeContents();
        }

        //this.gnssClock = gnssClock;
    }

    public String getTimeAccuracy() {
        return timeAccuracy;
    }

    public void setTimeAccuracy(String timeAccuracy) {
        this.timeAccuracy = timeAccuracy;
    }

    public String getTimeDisplay() {
        return timeDisplay;
    }

    public void setTimeDisplay(String timeDisplay) {
        this.timeDisplay = timeDisplay;
    }

    public String getTimeUnits() {
        return timeUnits;
    }

    public void setTimeUnits(String timeUnits) {
        this.timeUnits = timeUnits;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public static TimeAndLocation newInstance(Collection<GnssMeasurement> measurements) {
        return new TimeAndLocation(measurements);
    }
}
