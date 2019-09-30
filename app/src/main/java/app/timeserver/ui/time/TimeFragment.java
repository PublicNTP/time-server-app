package app.timeserver.ui.time;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.timeserver.R;
import app.timeserver.databinding.FragmentTimeBinding;
import app.timeserver.helper.DateFormatter;
import app.timeserver.helper.preferences.LocationCoordinateTypeStore;
import app.timeserver.helper.preferences.TimezoneStore;
import app.timeserver.repository.location.LocationStorageConsumer;
import app.timeserver.repository.location.converters.CoordinateConverter;
import app.timeserver.repository.time.TimeStorageConsumer;
import app.timeserver.ui.BaseFragment;

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
    @BindView(R.id.time_text_accuracy_units) TextView TimeTextAccuracyUnits;
    @BindView(R.id.time_image_logo) GifImageView spinningLogo;
    @BindView(R.id.time_text_time_zone) TextView timezoneDisplay;
    @BindView(R.id.time_logo_text) TextView timeLogoText;

    private GifDrawable spinningDrawable;
    private String[] timeZoneChoices;
    private String[] locationChoices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false);
        viewBinding.setTimestorage(new TimeStorageConsumer());
        locationChoices = getResources().getStringArray(R.array.location_choices);
        viewBinding.setLocationstorage(new LocationStorageConsumer(new LocationCoordinateTypeStore().getConverter(getContext(), locationChoices)));
        ButterKnife.bind(this, viewBinding.getRoot());


        timeZoneChoices = getResources().getStringArray(R.array.timezone_choices);
        DateFormatter.setTimezonePreference(new TimezoneStore().get(getContext(), timeZoneChoices));
        initializeSpinningLogo();

        return viewBinding.getRoot();
    }

    @OnClick(R.id.time_layout_logo)
    public void timeLayoutLogoOnClick() {
        playLogoOnce();
    }

    @OnClick(R.id.time_options)
    public void timeOptionsOnClick() {
        OptionsDialogFragment optionsDialogFragment = new OptionsDialogFragment();
        optionsDialogFragment.setOnOptionPicked(new OptionsDialogFragment.OnOptionPicked() {
            @Override
            public void onTimezonePicked(Integer timezone) {
                timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext(), timeZoneChoices));
            }

            public void onMeasurementPicked(Integer measurement) {
                TimeTextAccuracyUnits.setText(measurement.equals(0) ? "m" : "ft");
                LocationStorageConsumer.measurement = measurement;
            }

            @Override
            public void onLocationPicked(Integer units) {
                viewBinding.setLocationstorage(new LocationStorageConsumer(CoordinateConverter.byName(units)));
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null) {
            optionsDialogFragment.show(fragmentManager, "OptionsFragment");
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

        timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext(), timeZoneChoices));

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
