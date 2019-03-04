package org.app.timeserver.ui.satellite;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.timeserver.R;
import org.app.timeserver.model.SatelliteModel;
import org.app.timeserver.repository.location.LocationStorage;
import org.app.timeserver.ui.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zac on 4/3/18.
 */

public class SatelliteDetailFragment extends BaseFragment {
    private SatelliteModel satelliteModel;
    private OnDetailsClosedListener onDetailsClosedListener;

    @BindView(R.id.satellite_title) TextView titleView;
    @BindView(R.id.satellite_signal_to_noise) TextView signalToNoiseView;
    @BindView(R.id.satellite_altitude) TextView altitudeAngleView;
    @BindView(R.id.satellite_heading) TextView headingView;

    public static SatelliteDetailFragment newInstance(SatelliteModel satelliteModel, OnDetailsClosedListener onDetailsClosedListener) {
        SatelliteDetailFragment satelliteDetailFragment = new SatelliteDetailFragment();
        satelliteDetailFragment.setSatelliteModel(satelliteModel);
        satelliteDetailFragment.setOnDetailsClosedListener(onDetailsClosedListener);
        LocationStorage.setSelectedSatellite(satelliteModel);
        return satelliteDetailFragment;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_satellite_detail, container, false);
        ButterKnife.bind(this, rootView);

        titleView.setText(String.format("%s %d", satelliteModel.constellationName(), satelliteModel.svn));
        altitudeAngleView.setText(String.format("%.0f° upwards", satelliteModel.elevationDegrees));
        headingView.setText(String.format("%.0f° from North", satelliteModel.azimuthDegrees));
        signalToNoiseView.setText(String.format("%.1f to 1", satelliteModel.Cn0DbHz));

        return rootView;
    }

    @OnClick(R.id.satellite_detail_close)
    public void close() {
        LocationStorage.clearSelected();
        onDetailsClosedListener.onDetailClose();
    }

    public void setSatelliteModel(SatelliteModel satelliteModel) {
        this.satelliteModel = satelliteModel;
    }

    public void setOnDetailsClosedListener(OnDetailsClosedListener onDetailsClosedListener) {
        this.onDetailsClosedListener = onDetailsClosedListener;
    }

    public interface OnDetailsClosedListener {
        void onDetailClose();
    }
}
