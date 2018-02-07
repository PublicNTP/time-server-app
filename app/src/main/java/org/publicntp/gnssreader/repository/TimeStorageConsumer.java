package org.publicntp.gnssreader.repository;

import android.content.Context;

import org.publicntp.gnssreader.repository.TimeStorage;

/**
 * Created by zac on 2/7/18.
 */

public class TimeStorageConsumer {
    public TimeStorageConsumer() {}

    public String getAdjustedDateString(Context context) {
        return TimeStorage.getAdjustedDateString(context);
    }

    public String getDateDifference() {
        try {
            double difference = (double) TimeStorage.getDateDifference();
            return String.format("±%.2f", (float) difference / 1000);
        } catch (Exception e) {
            return String.format(e.getMessage());
        }
    }
}
