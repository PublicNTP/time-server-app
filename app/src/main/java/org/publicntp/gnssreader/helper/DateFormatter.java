package org.publicntp.gnssreader.helper;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by zac on 2/7/18.
 */

public class DateFormatter {
    private static final DateFormat hhmmss = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat hhmmssUtc = new SimpleDateFormat("HH:mm:ss");
    static {
        hhmmssUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String dateString(Context context, Date date) {
        if (date == null) return "";
        return dateString(LocaleHelper.getUserLocale(context), date);
    }

    public static String dateString(Locale locale, Date date) {
        if (date == null) return "";
        DateFormat dateTimeFormat = SimpleDateFormat.getDateInstance(java.text.DateFormat.MEDIUM, locale);
        return dateTimeFormat.format(date);
    }

    public static String dateTimeString(Context context, Date date) {
        if (date == null) return "";
        return dateTimeString(LocaleHelper.getUserLocale(context), date);
    }

    public static String dateTimeString(Locale locale, Date date) {
        if (date == null) return "";
        DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        return dateTimeFormat.format(date);
    }

    public static String timeString(Context context, Date date) {
        if (date == null) return "";
        return timeString(LocaleHelper.getUserLocale(context), date);
    }

    public static String utcTimeString(Context context, Date date) {
        if (date == null) return "";
        return utcTimeString(LocaleHelper.getUserLocale(context), date);
    }

    public static String timeString(Locale locale, Date date) {
        if (date == null) return "";
        return hhmmss.format(date);
    }

    public static String utcTimeString(Locale locale, Date date) {
        if (date == null) return "";
        return hhmmssUtc.format(date);
    }
}


