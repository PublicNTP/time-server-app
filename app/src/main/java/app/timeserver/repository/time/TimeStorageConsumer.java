package app.timeserver.repository.time;

import android.annotation.SuppressLint;
import android.content.Context;

import app.timeserver.service.ntp.NtpService;

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

    public String getDefaultDateDifferenceString() {
        return "±--";
    }

    public String getDateStringIfServiceRunning(Context context) {
        if(NtpService.exists()) {
            return getAdjustedDateString(context);
        } else {
            return getDefaultDateString();
        }
    }

    public Boolean isServiceRunning() {
      return NtpService.exists();
    }

    @SuppressLint("DefaultLocale")
    public String getDateDifference() {
        try {
            double difference = (double) TimeStorage.getDateDifference();
            return String.format("±%.2f", (float) difference / 1000);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getDateDifferenceIfServiceRunning() {
        if(NtpService.exists()) {
            return getDateDifference();
        } else {
            return getDefaultDateDifferenceString();
        }
    }

    public long getTime() {
        return TimeStorage.getTime();
    }
}
