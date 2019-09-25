package app.timeserver.helper.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zac on 1/31/18.
 */

public abstract class StringPreferenceStore {
    public String getKey() {
        throw new RuntimeException("Unimplemented");
    }

    public Integer getDefault() {
        throw new RuntimeException("Unimplemented");
    }

    public void set(Context context, Integer value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(getKey(), value).commit();
    }

    public Integer get(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getKey(), getDefault());
    }
}
