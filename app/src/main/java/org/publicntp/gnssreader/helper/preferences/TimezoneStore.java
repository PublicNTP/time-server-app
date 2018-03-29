package org.publicntp.gnssreader.helper.preferences;

import android.content.Context;

import org.publicntp.gnssreader.helper.DateFormatter;

/**
 * Created by zac on 3/29/18.
 */

public class TimezoneStore extends StringPreferenceStore {
    @Override
    public String getKey() {
        return "TIMEZONE";
    }

    @Override
    public String getDefault() {
        return "UTC";
    }

    @Override
    public void set(Context context, String value) {
        super.set(context, value);
        DateFormatter.setTimezonePreference(value);
    }
}
