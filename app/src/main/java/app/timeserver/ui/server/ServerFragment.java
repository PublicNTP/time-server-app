package app.timeserver.ui.server;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Switch;
import android.support.v7.widget.SwitchCompat;
import android.widget.Spinner;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.content.IntentFilter;
import android.util.Log;
import android.graphics.Typeface;

import app.timeserver.R;
import app.timeserver.databinding.FragmentServerBinding;
import app.timeserver.helper.NetworkInterfaceHelper;
import app.timeserver.helper.TimeMillis;
import app.timeserver.helper.Winebar;
import app.timeserver.helper.preferences.TimezoneStore;
import app.timeserver.repository.time.TimeStorageConsumer;
import app.timeserver.service.ntp.NtpService;
import app.timeserver.service.ntp.logging.ServerLogDataPointGrouper;
import app.timeserver.service.ntp.logging.ServerLogMinuteSummary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import eu.chainfire.libsuperuser.Shell;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class ServerFragment extends Fragment {
    public Context mContext;
    FragmentServerBinding viewBinding;
    Intent ntp_intent;
    Timer invalidationTimer;
    Handler uiHandler;
    final int invalidationFrequency = 1;

    Timer updatePacketsTimer;
    final int updatePacketsFrequency = (int) (.5f * TimeMillis.SECOND);

    Timer graphRefreshTimer;
    final int graphRefreshFrequency = (int) (3 * TimeMillis.SECOND);
    RefreshGraphTask refreshGraphTask;
    private boolean graphHasBeenInit = false;


    private String stratumChoice;
    private String networkChoice;
    private String packetChoice;
    private Boolean autoStart;
    private Boolean autoInit;

    @BindView(R.id.server_switch) SwitchCompat switchButton;
    @BindView(R.id.server_bar_graph) ColumnChartView barChartView;
    @BindView(R.id.server_display_time_zone) TextView timezoneDisplay;
    @BindView(R.id.server_display_net_activity) TextView activityDisplay;
    @BindView(R.id.server_port_interface) TextView serverPort;


    @BindColor(R.color.packet_outgoing_green) int outgoing_green;
    @BindColor(R.color.packet_incoming_purple) int incoming_purple;
    @BindColor(R.color.black) int black;
    @BindColor(R.color.white) int white;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getContext();
        stratumChoice = getPrefs(mContext).getString("stratumChoice", "1").toString();
        networkChoice = getPrefs(mContext).getString("networkChoice", "wlan0").toString();
        packetChoice = getPrefs(mContext).getString("packetChoice", "Unlimited").toString();
        autoStart = getPrefs(mContext).getBoolean("autoStart", false);
        autoInit = false;
        super.onCreate(savedInstanceState);

    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_server, container, false);
        viewBinding.setServertimestorage(new TimeStorageConsumer());
        ButterKnife.bind(this, viewBinding.getRoot());
        ntp_intent = new Intent(getContext(), NtpService.class);
        if(autoStart && autoInit == false){
          switchButton.setChecked(true);
          autoInit = true;
        }
        return viewBinding.getRoot();
    };

    @OnClick(R.id.sntp_options)
    public void sntpOptionsOnClick() {
        NtpService ntpService = NtpService.getNtpService();
        ServerDialogFragment serverDialogFragment = new ServerDialogFragment();

        serverDialogFragment.setOnOptionPicked(new ServerDialogFragment.OnOptionPicked() {
            @Override
            public void onStratumPicked(String option) {
              if(ntpService != null){
                ntpService.setStratumNumber(option);
              }
            }
            public void onNetworkPicked(String option) {
                if(ntpService != null){
                  ntpService.changeNetwork(option);
                }
            }
            public void onPacketPicked(String option) {
              String value = option;
              if(ntpService != null){
                if(option.equals("Unlimited")){
                  value = "100000";
                }
                ntpService.limitPackets(value);
              }
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null) {
            serverDialogFragment.show(fragmentManager, "OptionsFragment");
        }

    };

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("ServerPreferences", Context.MODE_PRIVATE);
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
    };

    @Override
    public void onResume() {
        uiHandler = new Handler();

        scheduleUIInvalidation();
        scheduleUpdatePacketCounter();
        scheduleGraphRefresh();
        buildDropdown();

        timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext()));
        switchButton.setChecked(NtpService.getNtpService() != null);
        super.onResume();
    };

    @Override
    public void onPause() {
        super.onPause();

        invalidationTimer.cancel();
        graphRefreshTimer.cancel();
        updatePacketsTimer.cancel();
    };

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    private void scheduleUIInvalidation() {
        invalidationTimer = new Timer();
        invalidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
                uiHandler.post(() -> {
                    boolean serviceExists = NtpService.exists();
                });
            }
        }, 0, invalidationFrequency);
    };

    private void scheduleGraphRefresh() {
        graphRefreshTimer = new Timer();
        graphRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
                refreshGraphTask = new RefreshGraphTask(ServerFragment.this);
                if(refreshGraphTask == null)
                  {
                       return; //or return false/true, based on where you are calling from
                  }else{
                    try{
                      refreshGraphTask.execute();
                    }
                    catch (Exception e) {
                       Log.e("refreshGraphTask","exception", e);
                    }
                  }

            }
        }, 0, graphRefreshFrequency);
    };

    private void scheduleUpdatePacketCounter() {
        updatePacketsTimer = new Timer();
        updatePacketsTimer.schedule(new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                uiHandler.post(() -> {
                    if (NtpService.exists()) {
                        ServerLogMinuteSummary currentMinute = ServerLogDataPointGrouper.mostRecent();
                        activityDisplay.setText(String.format("%d", currentMinute.totalCount()));
                    } else {
                        activityDisplay.setText("-");
                    }
                });
            }
        }, 0, updatePacketsFrequency);
    };
    public void startNTPService() {
      NtpService ntpService = NtpService.getNtpService();
      boolean hasReliableConnection = new NetworkInterfaceHelper().hasConnectivityOnAnyOf(Arrays.asList("eth0", "wlan0","usb0"));
      try {
          IntentFilter serviceFilter= new IntentFilter();
          serviceFilter.addAction(NtpService.SERVICE_ACTION);
          serviceFilter.addAction(NtpService.RESTART_ACTION);
          getContext().startService(ntp_intent);
          getContext().registerReceiver(broadcastReceiver, serviceFilter);
          Log.i("NTP", "startNTPService SERVICE");
          graphHasBeenInit = true;
      } catch (IllegalArgumentException e) {
          // Check wether we are in debug mode
          Log.i("NTP", "FAILED TO startNTPService");
      }
      if (!Shell.SU.available()) {
          Winebar.make(switchButton, R.string.no_root_warning, Snackbar.LENGTH_LONG).setAction("Help", v -> {
              launchWebUrl("https://www.xda-developers.com/root/");
          }).setActionTextColor(white).show();
      }
    }
    @OnCheckedChanged(R.id.server_switch)
    public void toggleServer() {
        NtpService ntpService = NtpService.getNtpService();
        if (switchButton.isChecked()) {
          if (ntpService == null) {
              startNTPService();
          }
        } else {

            if (ntpService != null) {
              try {
                getContext().unregisterReceiver(broadcastReceiver);
              }catch(IllegalArgumentException e) {
                e.printStackTrace();
              }
              getContext().stopService(ntp_intent);
            }
        }
    }
    protected void launchWebUrl(String webAddress) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webAddress));

        try {
            startActivity(Intent.createChooser(
                    intent, getString(R.string.chooser_app_weburl)));

        } catch (ActivityNotFoundException ex) {
            Toast.makeText(
                    getContext(),
                    getString(R.string.error_web_browser_not_found),
                    Toast.LENGTH_SHORT).show();
        }
    };
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
            if (fragmentReference.get().graphHasBeenInit || logData.stream().anyMatch(summmary -> summmary.totalCount() > 0)) {
                fragmentReference.get().uiHandler.post(() -> fragmentReference.get().initBarChart(logData));
                fragmentReference.get().graphHasBeenInit = true;
            }
        }
    };


    private void buildDropdown(){
      String port = NtpService.port;
      String chosenInterface = NtpService.chosenInterface;
      String ipAddress = NtpService.ipAddress;

      if (chosenInterface != "") {
          serverPort.setText(String.format("Running on %s, %s:%s", chosenInterface, ipAddress, port));
      }
    };

    private void updatePrefrences(){
      NtpService ntpService = NtpService.getNtpService();
      ntpService.setStratumNumber(stratumChoice);
      ntpService.changeNetwork(networkChoice);
      if(packetChoice.equals("Unlimited")){
        packetChoice = "100000";
      }
      ntpService.limitPackets(packetChoice);
    }
    private void updateFragment(){
      uiHandler = new Handler();
      updatePrefrences();
      buildDropdown();
      scheduleUIInvalidation();
      scheduleUpdatePacketCounter();
      scheduleGraphRefresh();
      timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext()));
    };

    private void restartService(){
      NtpService ntpService = NtpService.getNtpService();
      if (ntpService == null) {
          startNTPService();
      }else {
          try {
            getContext().unregisterReceiver(broadcastReceiver);
          }catch(IllegalArgumentException e) {
            e.printStackTrace();
          }
          getContext().stopService(ntp_intent);
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      private static final String TAG = "ServerActivity";
        @Override
        public void onReceive(Context context, Intent intent) {
          final String action = intent.getAction();
          if("START_NTP_SERVICE".equals(action)){
            Log.i("NTP", "NTP start received.");
            updatePrefrences();
            updateFragment();
          }else if ("RESTART_NTP_SERVICE".equals(action)){
            Log.i("NTP", "NTP restart received.");
            restartService();
          }

        }
    };


}
