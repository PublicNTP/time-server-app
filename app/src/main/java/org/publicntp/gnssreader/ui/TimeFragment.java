package org.publicntp.gnssreader.ui;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.databinding.FragmentTimeBinding;
import org.publicntp.gnssreader.repository.LocationStorageConsumer;
import org.publicntp.gnssreader.repository.TimeStorageConsumer;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TimeFragment extends BaseFragment {

    private TimeViewModel viewModel;

    FragmentTimeBinding viewBinding;
    Timer invalidationTimer;
    final int invalidationFrequency = 1;

    @BindView(R.id.time_text_time_display) TextView TimeTextDisplay;
    @BindView(R.id.time_image_logo) GifImageView spinningLogo;

    private GifDrawable spinningDrawable;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TimeViewModel.class);

//        TextView textLatitude = getView().findViewById(R.id.time_text_latitude_display);
//        TextView textLongitude = getView().findViewById(R.id.time_text_longitude_display);

//        if (viewModel != null) {
//            viewModel.init();
//            viewModel.getLocation().observe(this, location -> {
//                Timber.d("location updated: %s", location.toString());
//                textLatitude.setText(String.valueOf(location.getLatitude()));
//                textLongitude.setText(String.valueOf(location.getLongitude()));
//            });
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false);
        viewBinding.setTimestorage(new TimeStorageConsumer());
        viewBinding.setLocationstorage(new LocationStorageConsumer());
        ButterKnife.bind(this, viewBinding.getRoot());

        initializeSpinningLogo();

        return viewBinding.getRoot();
    }

    @OnClick(R.id.time_layout_logo)
    public void timeLayoutLogoOnClick() {
        playLogoOnce();
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
