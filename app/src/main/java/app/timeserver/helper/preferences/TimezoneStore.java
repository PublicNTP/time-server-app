package app.timeserver.helper.preferences;

import android.content.Context;
import android.icu.util.TimeZone;

import app.timeserver.helper.DateFormatter;
import app.timeserver.helper.LocaleHelper;

import java.util.Date;
import java.util.Locale;

public class TimezoneStore extends StringPreferenceStore {
    @Override
    public String getKey() {
        return "TIMEZONE";
    }

    @Override
    public Integer getDefault() {
        return 0;
    }

    @Override
    public void set(Context context, Integer value) {
        super.set(context, value);
        DateFormatter.setTimezonePreference(value);
    }

    public String getTimeZoneShortName(Context context) {
        return getTimeZoneShortName(context, new TimezoneStore().get(context));
    }

    public String getTimeZoneShortName(Context context, Integer zone) {
        if (zone.equals(0)) {
            return zone;
        }else if (zone.equals(1)) {
            return "decMs";
        }else if (zone.equals(2)) {
            return ".beats";
        } else {
            Locale locale = LocaleHelper.getUserLocale(context);
            TimeZone tz = TimeZone.getDefault();
            boolean dstActive = tz.inDaylightTime(new Date());
            return tz.getDisplayName(dstActive, TimeZone.SHORT, locale);
        }
    }
}
