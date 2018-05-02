package org.publicntp.gnssreader.helper.preferences;

import android.content.Context;

import org.publicntp.gnssreader.repository.location.converters.CoordinateConverter;

public class LocationCoordinateTypeStore extends StringPreferenceStore {
    public String getKey() {
        return "LOCATION_COORDINATE_TYPE";
    }

    @Override
    public String getDefault() {
        return "Lat/Long";
    }

    public CoordinateConverter getConverter(Context context) {
        return CoordinateConverter.byName(this.get(context));
    }
}
