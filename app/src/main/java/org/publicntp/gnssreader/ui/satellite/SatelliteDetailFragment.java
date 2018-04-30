package org.publicntp.gnssreader.ui.satellite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.BaseFragment;

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
    @BindView(R.id.satellite_svn) TextView svnView;
    @BindView(R.id.satellite_constellation) TextView constellationView;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_satellite_detail, container, false);
        ButterKnife.bind(this, rootView);

        titleView.setText(String.format("Satellite %s-%d", satelliteModel.constellationName(), satelliteModel.svn));
        svnView.setText("" + satelliteModel.svn);
        constellationView.setText(satelliteModel.constellationName());
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
