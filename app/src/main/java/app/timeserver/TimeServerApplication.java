package app.timeserver;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;


public class TimeServerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    // Logs crash data for reporting
    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {

            // Ignore VERBOSE and DEBUG log messages
            if (priority <= Log.DEBUG) {
                return;
            }

            // TODO: Call crash reporting library

        }
    }
}
