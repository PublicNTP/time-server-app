package app.timeserver.repository.location;

import android.content.Context;
import android.annotation.SuppressLint;

import app.timeserver.repository.location.converters.CoordinateConverter;
import app.timeserver.repository.location.converters.LatLongConverter;
import app.timeserver.helper.preferences.MeasurementStore;
/**
 * Created by zac on 2/7/18.
 */

public class LocationStorageConsumer {
    public static String measurement = "Metric/SI";

    private CoordinateConverter coordinateConverter;

    public LocationStorageConsumer() {
        coordinateConverter = new LatLongConverter();
    }

    public LocationStorageConsumer(CoordinateConverter coordinateConverter) {
        this.coordinateConverter = coordinateConverter;
    }

    @SuppressLint("DefaultLocale")
    public String getString() {
        if (LocationStorage.isPopulated()) {
            double latitude = LocationStorage.getLatitude();
            double longitude = LocationStorage.getLongitude();
            return coordinateConverter.getString(latitude, longitude);
        } else {
            return "--°\n--°";
        }
    }

    @SuppressLint("DefaultLocale")
    public String getHumanReadableLocation() {
        return coordinateConverter.clipboardString(LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getSharableLocation() {
        return String.format("geo:%.4f,%.4f", LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getStringError() {
        if(LocationStorage.isPopulated()) {
            float error = LocationStorage.getAccuracy();
            if(measurement.equals("Imperial/US")){
              double feet = error * 3.28084;
              return String.format("±%.2f", feet);
            }else{
              return String.format("±%.2f", error);
            }
        } else {
            return "±--";
        }
    }
}
