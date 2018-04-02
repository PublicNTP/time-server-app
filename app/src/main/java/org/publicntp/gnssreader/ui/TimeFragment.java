package org.publicntp.gnssreader.ui;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.databinding.FragmentTimeBinding;
import org.publicntp.gnssreader.helper.DateFormatter;
import org.publicntp.gnssreader.helper.LocaleHelper;
import org.publicntp.gnssreader.helper.preferences.TimezoneStore;
import org.publicntp.gnssreader.repository.LocationStorageConsumer;
import org.publicntp.gnssreader.repository.TimeStorageConsumer;
import org.publicntp.gnssreader.ui.custom.SettingsDialogFragment;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TimeFragment extends BaseFragment {

    FragmentTimeBinding viewBinding;
    Timer invalidationTimer;
    final int invalidationFrequency = 1;

    @BindView(R.id.time_text_time_display) TextView TimeTextDisplay;
    @BindView(R.id.time_image_logo) GifImageView spinningLogo;
    @BindView(R.id.time_text_time_zone) TextView timezoneDisplay;

    private GifDrawable spinningDrawable;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false);
        viewBinding.setTimestorage(new TimeStorageConsumer());
        viewBinding.setLocationstorage(new LocationStorageConsumer());
        ButterKnife.bind(this, viewBinding.getRoot());

        DateFormatter.setTimezonePreference(new TimezoneStore().get(getContext()));
        initializeSpinningLogo();

        return viewBinding.getRoot();
    }

    @OnClick(R.id.time_layout_logo)
    public void timeLayoutLogoOnClick() {
        playLogoOnce();
    }

    @OnClick(R.id.time_options)
    public void timeOptionsOnClick() {
        SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
        settingsDialogFragment.setOnOptionPicked(new SettingsDialogFragment.OnOptionPicked() {
            @Override
            public void onTimezonePicked(String timezone) {
                setTimezoneDisplayText(timezone);
            }

            @Override
            public void onLocationPicked(String units) {}
        });
        settingsDialogFragment.show(getFragmentManager(), "OptionsFragment");
    }

    private void setTimezoneDisplayText(String zone) {
        if(zone.equals("UTC")) {
            timezoneDisplay.setText(zone);
        } else {
            Locale locale = LocaleHelper.getUserLocale(getContext());
            timezoneDisplay.setText(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, locale));
        }
    }

    private void initializeSpinningLogo() {
        spinningDrawable = (GifDrawable) spinningLogo.getDrawable();
        playLogoOnce();
    }

    private void playLogoOnce() {
        spinningDrawable.stop();
        spinningDrawable.setLoopCount(1);
        spinningDrawable.seekTo(400);
        spinningDrawable.start();
    }

    @Override
    public void onResume() {
        invalidationTimer = new Timer();
        invalidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
            }
        }, invalidationFrequency, invalidationFrequency);

        setTimezoneDisplayText(new TimezoneStore().get(getContext()));
        super.onResume();
    }

    @Override
    public void onPause() {
        invalidationTimer.cancel();
        super.onPause();
    }

    public static TimeFragment newInstance() {
        return new TimeFragment();
    }
}
