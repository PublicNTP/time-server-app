package org.publicntp.gnssreader.ui;


import android.arch.lifecycle.ViewModel;
import android.content.Context;

import org.publicntp.gnssreader.model.TimeAndLocation;
import org.publicntp.gnssreader.repository.TimeAndLocationRepository;


public class TimeViewModel extends ViewModel {

    private TimeAndLocation timeAndLocation;
    private TimeAndLocationRepository repository;

//    public TimeViewModel(Context context) {
//        repository = new TimeAndLocationRepository(context);
//    }

    public TimeAndLocation getTimeAndLocation() {

        if (timeAndLocation == null) {
            //timeAndLocation = repository.getCurrentTimeAndLocation();
        }

        return timeAndLocation;
    }


}
