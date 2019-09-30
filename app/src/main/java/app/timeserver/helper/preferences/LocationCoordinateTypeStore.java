package app.timeserver.helper.preferences;

import android.content.Context;

import app.timeserver.repository.location.converters.CoordinateConverter;

public class LocationCoordinateTypeStore extends StringPreferenceStore {
    public String getKey() {
        return "LOCATION_COORDINATE_TYPE";
    }

    @Override
    public Integer getDefault() {
        return 0;
    }

    public CoordinateConverter getConverter(Context context, String[] choices) {
        return CoordinateConverter.byName(this.get(context, choices));
    }
}
