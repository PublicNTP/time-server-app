package org.publicntp.gnssreader.ui;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.databinding.FragmentServerBinding;
import org.publicntp.gnssreader.helper.RootChecker;
import org.publicntp.gnssreader.helper.TimeMillis;
import org.publicntp.gnssreader.helper.Winebar;
import org.publicntp.gnssreader.helper.preferences.TimezoneStore;
import org.publicntp.gnssreader.repository.TimeStorageConsumer;
import org.publicntp.gnssreader.service.ntp.NtpService;
import org.publicntp.gnssreader.service.ntp.log.ServerLogDataPointGrouper;
import org.publicntp.gnssreader.service.ntp.log.ServerLogMinuteSummary;
import org.publicntp.gnssreader.ui.custom.OptionsDialogFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindColor;
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

    Timer updatePacketsTimer;
    final int updatePacketsFrequency = (int) (.5f * TimeMillis.SECOND);

    Timer graphRefreshTimer;
    final int graphRefreshFrequency = (int) (3 * TimeMillis.SECOND);
    RefreshGraphTask refreshGraphTask;
    private boolean graphHasBeenInit = false;

    @BindView(R.id.server_btn_toggle) ToggleButton toggleServerButton;
    @BindView(R.id.server_bar_graph) ColumnChartView barChartView;
    @BindView(R.id.server_display_time_zone) TextView timezoneDisplay;
    @BindView(R.id.server_display_net_activity) TextView activityDisplay;

    @BindColor(R.color.packet_outgoing_green) int outgoing_green;
    @BindColor(R.color.packet_incoming_purple) int incoming_purple;
    @BindColor(R.color.black) int black;

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


    private void initBarChart(List<ServerLogMinuteSummary> data) {
        List<Column> columns = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        int counter = 0; //Hello-charts prefers to have incremental x values with calculated x labels
        for (ServerLogMinuteSummary minuteSummary : data) {
            SubcolumnValue receivedValue = new SubcolumnValue(minuteSummary.inbound).setColor(incoming_purple);
            SubcolumnValue sentValue = new SubcolumnValue(minuteSummary.outbound).setColor(outgoing_green);
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            subcolumnValues.add(receivedValue);
            subcolumnValues.add(sentValue);

            Column column = new Column().setValues(subcolumnValues);
            AxisValue axisValue = new AxisValue(counter).setLabel("" + ((minuteSummary.timeReceived - startTime) / TimeMillis.MINUTE));

            columns.add(column);
            axisValues.add(axisValue);
            counter++;
        }
        ColumnChartData columnChartData = new ColumnChartData(columns);
        columnChartData.setStacked(true);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setHasLines(true);
        xAxis.setName("Minutes Ago");
        xAxis.setTextColor(black);

        Axis yAxis = new Axis();
        yAxis.setHasLines(true);
        yAxis.setTextColor(black);

        columnChartData.setAxisXBottom(xAxis);
        columnChartData.setAxisYLeft(yAxis);

        barChartView.setColumnChartData(columnChartData);
        barChartView.setZoomEnabled(false);
    }

    @Override
    public void onResume() {
        uiHandler = new Handler();

        invalidationTimer = new Timer();
        invalidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
                uiHandler.post(() -> {
                    boolean serviceExists = NtpService.exists();
                    toggleServerButton.setChecked(serviceExists);
                    toggleServerButton.setText(serviceExists ? "Server On" : "Server Off");
                });
            }
        }, 0, invalidationFrequency);

        updatePacketsTimer = new Timer();
        updatePacketsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                uiHandler.post(() -> {
                    if (NtpService.exists()) {
                        ServerLogMinuteSummary currentMinute = ServerLogDataPointGrouper.mostRecent();
                        activityDisplay.setText("" + currentMinute.getTotal());
                    } else {
                        activityDisplay.setText("-");
                    }
                });
            }
        }, 0, updatePacketsFrequency);

        graphRefreshTimer = new Timer();
        graphRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
                refreshGraphTask = new RefreshGraphTask(ServerFragment.this);
                refreshGraphTask.execute();
            }
        }, 0, graphRefreshFrequency);

        timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext()));
        toggleServerButton.setChecked(NtpService.getNtpService() != null);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        invalidationTimer.cancel();
        graphRefreshTimer.cancel();
        updatePacketsTimer.cancel();
    }

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @OnCheckedChanged(R.id.server_btn_toggle)
    public void toggleServer() {
        NtpService ntpService = NtpService.getNtpService();
        if (toggleServerButton.isChecked()) {
            if (ntpService == null) {
                getActivity().startService(NtpService.ignitionIntent(getContext()));
                graphHasBeenInit = true;
                if (!RootChecker.isRootGiven()) {
                    Winebar.make(toggleServerButton, R.string.no_root_warning, Snackbar.LENGTH_LONG).setAction("Help", v -> {

                    }).setActionTextColor(ContextCompat.getColor(getContext(), R.color.white)).show();
                }
            }
        } else {
            if (ntpService != null) {
                ntpService.stopSelf();
            }
        }
    }

    @OnClick(R.id.server_layout_time)
    public void timeOptionsOnClick() {
        OptionsDialogFragment optionsDialogFragment = new OptionsDialogFragment();
        optionsDialogFragment.setOnOptionPicked(new OptionsDialogFragment.OnOptionPicked() {
            @Override
            public void onTimezonePicked(String timezone) {
                timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext(), timezone));
            }

            @Override
            public void onLocationPicked(String units) {
            }
        });
        optionsDialogFragment.show(getFragmentManager(), "OptionsFragment");
    }

    private static class RefreshGraphTask extends AsyncTask<Void, Void, List<ServerLogMinuteSummary>> {
        private WeakReference<ServerFragment> fragmentReference;

        RefreshGraphTask(ServerFragment fragment) {
            super();
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<ServerLogMinuteSummary> doInBackground(Void... voids) {
            return ServerLogDataPointGrouper.oneHourSummary();
        }

        @Override
        protected void onPostExecute(List<ServerLogMinuteSummary> logData) {
            if (fragmentReference.get().graphHasBeenInit || logData.stream().anyMatch(summmary -> summmary.getTotal() > 0)) {
                fragmentReference.get().uiHandler.post(() -> fragmentReference.get().initBarChart(logData));
                fragmentReference.get().graphHasBeenInit = true;
            }
        }
    }
}
