package org.publicntp.gnssreader.repository;

import android.content.Context;

import org.publicntp.gnssreader.repository.TimeStorage;
import org.publicntp.gnssreader.service.ntp.NtpService;

import java.sql.Time;

/**
 * Created by zac on 2/7/18.
 */

public class TimeStorageConsumer {
    public TimeStorageConsumer() {}

    public String getAdjustedDateString(Context context) {
        return TimeStorage.getAdjustedDateString(context);
    }

    public String getDefaultDateString() {
        return TimeStorage.getDefaultDateString();
    }

    public String getDateStringIfServiceRunning(Context context) {
        if(NtpService.exists()) {
            return getAdjustedDateString(context);
        } else {
            return getDefaultDateString();
        }
    }

    public String getDateDifference() {
        try {
            double difference = (double) TimeStorage.getDateDifference();
            return String.format("±%.2f", (float) difference / 1000);
        } catch (Exception e) {
            return String.format(e.getMessage());
        }
    }

    public long getTime() {
        return TimeStorage.getTime();
    }
}