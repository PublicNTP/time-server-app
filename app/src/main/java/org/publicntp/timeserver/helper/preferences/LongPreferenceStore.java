package org.publicntp.timeserver.helper.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zac on 1/31/18.
 */

public abstract class LongPreferenceStore {
    public String getKey() {
        throw new RuntimeException("Unimplemented");
    }

    public Long getDefault() {
        throw new RuntimeException("Unimplemented");
    }

    public void set(Context context, Long value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(getKey(), value).commit();
    }

    public Long get(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(getKey(), getDefault());
    }
}
