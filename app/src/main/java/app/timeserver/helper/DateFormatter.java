package app.timeserver.helper;

import android.content.Context;

import app.timeserver.helper.preferences.TimezoneStore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

/**
 * Created by zac on 2/7/18.
 */

public class DateFormatter {
    private static final DateFormat hhmmssmm = new SimpleDateFormat("HH:mm:ss.SS");
    private static final DateFormat hhmmssmmUtc = new SimpleDateFormat("HH:mm:ss.SS");
    private static final DateFormat hhmmss = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat hhmmssUtc = new SimpleDateFormat("HH:mm:ss");
    static {
        hhmmssUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        hhmmssmmUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static Integer timezonePreference = new TimezoneStore().getDefault();

    public static void setTimezonePreference(Integer timezonePreference) {

        DateFormatter.timezonePreference = timezonePreference;
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



    public static String preferredTimeString(Context context, Date date) {
        if (date == null) return "";
        Locale locale = LocaleHelper.getUserLocale(context);

        if(timezonePreference.equals(0)) {
            return utcTimeString(locale, date);
        }else if(timezonePreference.equals(2)) {
            return getDecimalTime(date);
        }else if(timezonePreference.equals(3)) {
          return getSwatchTime(date);
        }else {
            return timeString(locale, date);
        }
    }

    public static String utcTimeString(Context context, Date date) {
        if (date == null) return "";
        return utcTimeString(LocaleHelper.getUserLocale(context), date);
    }

    public static String timeString(Locale locale, Date date) {
        if (date == null) return "";
        return hhmmssmm.format(date);
    }

    public static String utcTimeString(Locale locale, Date date) {
        if (date == null) return "";
        return hhmmssmmUtc.format(date);
    }
    public static String getSwatchTime(Date date) {
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC+01:00 "));
      Calendar now = Calendar.getInstance();
      Double beats = ( ( cal.get( now.SECOND ) + ( cal.get( now.MINUTE ) * 60 ) + ( cal.get( now.HOUR_OF_DAY ) * 3600 ) ) / 86.4 );
      return String.format("@ %6f", beats);
    }
    public static Double getWholeNumber(Double num){
      double fractionalPart = num % 1;
      return num - fractionalPart;
    }
    public static String getDecimalTime(Date date) {
      Integer millisPerSecond = 1000;
      Integer millisPerDecimalMinute = 100 * 1000;
      Integer millisPerDecimalHour = 100 * 100 * 1000;
      Integer millisPerDay = 24 * 60 * 60 * 1000;

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      // Get current milliseconds;
      Long nowMilli = cal.getTimeInMillis();

      //Set time to midnight
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      //get get milliseconds since midnight;
      Long timeMilli = nowMilli - cal.getTimeInMillis();

      Double decHr = (double)(timeMilli / millisPerDecimalHour);

      //define remaining time to remove milliseconds for hours
      Double timeRemaining = (double)(timeMilli - (Math.round(getWholeNumber(decHr)) * millisPerDecimalHour));

      Double decMin =(double)( timeRemaining / millisPerDecimalMinute);

      //Redefine remaining time to remove milliseconds for minutes
      timeRemaining = (double)(timeRemaining - (Math.round(getWholeNumber(decMin)) * millisPerDecimalMinute));

      //Everything remaining is seconds or milliseconds, we don't need milliseconds.
      Double decSec =(double)( timeRemaining / millisPerSecond);

      return Math.round(getWholeNumber(decHr)) + ":" + Math.round(getWholeNumber(decMin)) + ":"+ Math.round(getWholeNumber(decSec));

    }
}
