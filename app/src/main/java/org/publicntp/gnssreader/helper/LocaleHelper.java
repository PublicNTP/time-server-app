package org.publicntp.timeserver.helper;

import android.content.Context;
import android.os.LocaleList;

import java.util.Locale;

/**
 * Created by zac on 2/7/18.
 */

public class LocaleHelper {
    public static Locale getUserLocale(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            LocaleList locales = context.getResources().getConfiguration().getLocales();
            if (locales.size() > 0) return locales.get(0);
            else return Locale.US;
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }
}


