package org.publicntp.gnssreader.ui;

import android.databinding.DataBindingUtil;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.databinding.FragmentServerBinding;
import org.publicntp.gnssreader.helper.LocaleHelper;
import org.publicntp.gnssreader.helper.RootChecker;
import org.publicntp.gnssreader.helper.preferences.TimezoneStore;
import org.publicntp.gnssreader.repository.TimeStorageConsumer;
import org.publicntp.gnssreader.service.ntp.ServerLogDataPoint;
import org.publicntp.gnssreader.service.ntp.NtpService;
import org.publicntp.gnssreader.ui.custom.SettingsDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class ServerFragment extends Fragment {
    FragmentServerBinding viewBinding;

    Timer invalidationTimer;
    Handler uiHandler;
    final int invalidationFrequency = 1;

    Timer graphRefreshTimer;
    final int graphRefreshFrequency = 50;
    final int incrementEvery = 1000;
    Long lastIncrementedAt;

    @BindView(R.id.server_btn_toggle) ToggleButton toggleServerButton;
    @BindView(R.id.server_bar_graph) ColumnChartView barChartView;
    @BindView(R.id.server_display_time_zone) TextView timezoneDisplay;
    @BindView(R.id.server_display_net_activity) TextView activityDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_server, container, false);
        viewBinding.setServertimestorage(new TimeStorageConsumer());
        ButterKnife.bind(this, viewBinding.getRoot());

        return viewBinding.getRoot();
    }


    private void initBarChart(List<ServerLogDataPoint> data) {
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int counter = 0; //Hello-charts prefers to have incremental x values with calculated x labels
        for (ServerLogDataPoint serverLogDataPoint : data) {
            SubcolumnValue subcolumnValue = new SubcolumnValue(serverLogDataPoint.numberReceived);//.setLabel(key + "");
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            subcolumnValues.add(subcolumnValue);

            Column column = new Column().setValues(subcolumnValues);
            AxisValue axisValue = new AxisValue(counter).setLabel("" + (serverLogDataPoint.timeReceived - startTime) / 1000);

            columns.add(column);
            axisValues.add(axisValue);
            counter++;
        }
        ColumnChartData columnChartData = new ColumnChartData(columns);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setHasLines(true);
        xAxis.setName("Seconds Ago");

        Axis yAxis = new Axis();
        yAxis.setHasLines(true);

        columnChartData.setAxisXBottom(xAxis);
        columnChartData.setAxisYLeft(yAxis);

        barChartView.setColumnChartData(columnChartData);
        barChartView.setZoomEnabled(false);
    }

    private void setTimezoneDisplayText(String zone) {
        if (zone.equals("UTC")) {
            timezoneDisplay.setText(zone);
        } else {
            Locale locale = LocaleHelper.getUserLocale(getContext());
            timezoneDisplay.setText(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, locale));
        }
    }

    @Override
    public void onResume() {
        uiHandler = new Handler();

        invalidationTimer = new Timer();
        invalidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
            }
        }, invalidationFrequency, invalidationFrequency);

        graphRefreshTimer = new Timer();
        graphRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
                uiHandler.post(() -> {
                    if(NtpService.getNtpService() != null) {
                        List<ServerLogDataPoint> logData = NtpService.getNtpService().getServerLogData();
                        ServerLogDataPoint mostRecentData = NtpService.getNtpService().mostRecentData();
                        if (mostRecentData != null) {
                            activityDisplay.setText("" + mostRecentData.numberReceived);
                        }

                        if(lastIncrementedAt == null || System.currentTimeMillis() - lastIncrementedAt > incrementEvery) {
                            NtpService.getNtpService().incrementMockData();
                            lastIncrementedAt = System.currentTimeMillis();
                        }

                        initBarChart(logData);
                    } else {
                        initBarChart(new ArrayList<>());
                    }
                });
            }
        }, graphRefreshFrequency, graphRefreshFrequency);

        setTimezoneDisplayText(new TimezoneStore().get(getContext()));
        toggleServerButton.setChecked(NtpService.getNtpService() != null);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        invalidationTimer.cancel();
        graphRefreshTimer.cancel();
    }

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @OnCheckedChanged(R.id.server_btn_toggle)
    public void toggleServer() {
        if (!RootChecker.isRootGiven()) {
            Snackbar.make(toggleServerButton, R.string.no_root_warning, Snackbar.LENGTH_SHORT).show();
            //return;
        }

        NtpService ntpService = NtpService.getNtpService();
        if(toggleServerButton.isChecked()) {
            if (ntpService == null) {
                getActivity().startService(NtpService.ignitionIntent(getContext()));
                toggleServerButton.setText("Server On");
            }
        } else {
            if(ntpService != null) {
                ntpService.stopSelf();
                toggleServerButton.setText("Server Off");
            }
        }
    }

    @OnClick(R.id.server_layout_time)
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
}
