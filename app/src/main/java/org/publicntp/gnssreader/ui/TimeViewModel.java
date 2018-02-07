package org.publicntp.gnssreader.ui;


import android.arch.lifecycle.ViewModel;

import org.publicntp.gnssreader.model.TimeAndLocation;


public class TimeViewModel extends ViewModel {

    private TimeAndLocation timeAndLocation;

//    public TimeViewModel(Context context) {
//    }

    public TimeAndLocation getTimeAndLocation() {

        if (timeAndLocation == null) {
            //timeAndLocation = repository.getCurrentTimeAndLocation();
        }

        return timeAndLocation;
    }


}
