package org.publicntp.gnssreader.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.LocationManager;

import org.publicntp.gnssreader.model.TimeAndLocation;


public class TimeAndLocationRepository {

    private static LocationManager locationManager;

    public TimeAndLocationRepository(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public LiveData<TimeAndLocation> getCurrentTimeAndLocation() {

        final MutableLiveData<TimeAndLocation> timeAndLocation = new MutableLiveData<>();


        // TODO: Get current time and location

        return timeAndLocation;
    }
}
