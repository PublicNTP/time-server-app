package app.timeserver.ui.satellite;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import app.timeserver.R;
import app.timeserver.model.SatelliteModel;
import app.timeserver.repository.location.LocationStorage;
import app.timeserver.ui.BaseFragment;

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
    @BindView(R.id.satellite_frequency) TextView satelliteFrequency;


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
        altitudeAngleView.setText(String.format("%.0f° above horizon", satelliteModel.elevationDegrees));
        headingView.setText(String.format("%.0f° from North", satelliteModel.azimuthDegrees));
        signalToNoiseView.setText(String.format("%.1f dB", satelliteModel.snrInDb));
        // satelliteFrequency.setText(String.format("%s", satelliteModel.carrierFrequencyHz));

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
