package app.timeserver.helper.preferences;

import android.content.Context;

import app.timeserver.helper.DateFormatter;
import app.timeserver.helper.LocaleHelper;

import java.util.Locale;

public class MeasurementStore extends StringPreferenceStore {

    @Override
    public String getKey() {
        return "MEASUREMENT";
    }

    @Override
    public String getDefault() {
        return "Metric/SI";
    }

    @Override
    public void set(Context context, String value) {
        super.set(context, value);
    }

}
