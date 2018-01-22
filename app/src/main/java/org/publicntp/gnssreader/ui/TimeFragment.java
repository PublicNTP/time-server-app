package org.publicntp.gnssreader.ui;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.publicntp.gnssreader.R;
import org.w3c.dom.Text;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;


public class TimeFragment extends BaseFragment {

    private TimeViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TimeViewModel.class);

        TextView textLatitude = getView().findViewById(R.id.time_text_latitude_display);
        TextView textLongitude = getView().findViewById(R.id.time_text_longitude_display);

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
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static TimeFragment newInstance() {
        return new TimeFragment();
    }
}
