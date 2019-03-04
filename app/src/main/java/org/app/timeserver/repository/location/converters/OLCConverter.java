package org.app.timeserver.repository.location.converters;

import android.annotation.SuppressLint;

import com.google.openlocationcode.OpenLocationCode;

public class OLCConverter extends CoordinateConverter {
    @Override
    public String getString(double lat, double lon) {
        return new OpenLocationCode(lat, lon).getCode();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String clipboardString(double lat, double lon) {
        return getString(lat, lon);
    }
}
