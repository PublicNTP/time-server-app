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
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(getKey(), Integer.toString(value)).commit();
    }

    public Integer get(Context context, String[] choices) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Integer result = 0;
        try{
          result = Integer.parseInt(sharedPreferences.getString(getKey(), Integer.toString(getDefault())));
        }catch(Exception e) {
          String choice = sharedPreferences.getString(getKey(), Integer.toString(getDefault()));
          int i = 0;
          for(String s : choices) {
               if(s.equals(choice)) {
                   result = i;
                   break;
               }
               i++;
          }

        }
        return result;
    }
}
