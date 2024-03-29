package app.timeserver.ui.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.util.Log;
import app.timeserver.service.ntp.NtpService;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.timeserver.R;
import app.timeserver.helper.Winebar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnCheckedChanged;

/**
 * Created by Eric on 05/29/19.
 */

public class ServerDialogFragment extends DialogFragment {
    @BindView(R.id.stratum_options) Spinner stratumSpinner;
    @BindView(R.id.network_options) Spinner networkSpinner;
    @BindView(R.id.packet_options) Spinner packetSpinner;
    @BindView(R.id.auto_start_switch) Switch switchAutoStart;
    /*
    @BindView(R.id.port_options) Spinner portSpinner;
    @BindView(R.id.auto_start_switch) Switch switchButton;
    */

    private Activity activity;

    private String[] stratumChoices;
    private String[] networkChoices;
    private String[] interfaceChoices;
    private String[] packetChoices;
    private String stratumChoice;
    private String networkChoice;
    private String packetChoice;
    private Boolean autoStart;

    Context mContext;

    public interface OnOptionPicked {
      void onStratumPicked(String option);
      void onNetworkPicked(String network);
      void onPacketPicked(String packet);
      //void onPortPicked(String port);
    }

    private OnOptionPicked onOptionPicked;

    public void setOnOptionPicked(OnOptionPicked onOptionPicked) {
        this.onOptionPicked = onOptionPicked;
    }

    public void setSpinnerSelection(String selected, String[] choices, Spinner spinner) {
        int i = 0;
        for(String s : choices) {
            if(s.equals(selected)) {
                spinner.setSelection(i);
                break;
            }
            i++;
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();


        View rootView = inflater.inflate(R.layout.fragment_server_detail, null);
        ButterKnife.bind(this, rootView);

        NtpService ntpService = NtpService.getNtpService();

        stratumChoices = getResources().getStringArray(R.array.stratum_choices);
        stratumSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.stratum_choices, R.layout.spinner_item));

        packetChoices = getResources().getStringArray(R.array.packet_choices);
        packetSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.packet_choices, R.layout.spinner_item));


        ArrayList portList = ntpService.portList;

        //First Step: convert ArrayList to an Object array.
        Object[] networkNames = portList.toArray();

        //Second Step: convert Object array to String array
        networkChoices = Arrays.copyOf(networkNames, networkNames.length, String[].class);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, portList);
        networkSpinner.setAdapter(null);
        networkSpinner.setAdapter(adapter);

        stratumChoice = getPrefs(activity).getString("stratumChoice", stratumChoices[0]).toString();
        networkChoice = getPrefs(activity).getString("networkChoice", "wlan0").toString();
        packetChoice = getPrefs(activity).getString("packetChoice", packetChoices[0]).toString();
        autoStart = getPrefs(activity).getBoolean("autoStart", false);

        setSpinnerSelection(stratumChoice, stratumChoices, stratumSpinner);
        setSpinnerSelection(packetChoice, packetChoices, packetSpinner);
        setSpinnerSelection(networkChoice, networkChoices, networkSpinner);
        switchAutoStart.setChecked(autoStart);

        builder.setView(rootView);

        return builder.create();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("ServerPreferences", Context.MODE_PRIVATE);
    }

    public void setString(String PREF_NAME, String input) {
        SharedPreferences.Editor editor = getPrefs(activity).edit();
        editor.putString(PREF_NAME, input);
        editor.commit();
    }

    public void setBoolean(String PREF_NAME, Boolean input) {
        SharedPreferences.Editor editor = getPrefs(activity).edit();
        editor.putBoolean(PREF_NAME, input);
        editor.commit();
    }

    @OnItemSelected(R.id.stratum_options)
    public void onStratumClicked(AdapterView<?> parent, View view, int position, long id) {
      String units = stratumChoices[position];
      setString("stratumChoice", units);
      onOptionPicked.onStratumPicked(units);
    }
    @OnItemSelected(R.id.network_options)
    public void onNetworkClicked(AdapterView<?> parent, View view, int position, long id) {
      String units = networkChoices[position];
      setString("networkChoice", units);

      onOptionPicked.onNetworkPicked(units);
    }
    @OnItemSelected(R.id.packet_options)
    public void onPacketClicked(AdapterView<?> parent, View view, int position, long id) {
      String units = packetChoices[position];
      setString("packetChoice", units);

      onOptionPicked.onPacketPicked(units);
    }

    /*
    @OnItemSelected(R.id.port_options)
      public void onPortClicked(AdapterView<?> parent, View view, int position, long id) {
      String units = interfaceChoices[position];
      onOptionPicked.onPortPicked(units);
    }
    */

    @OnCheckedChanged(R.id.auto_start_switch)
    public void onAutoStartClicked() {
      setBoolean("autoStart", switchAutoStart.isChecked());
    }


    @OnClick(R.id.server_options_done)
    public void dismissOptions() {
        this.dismiss();
    }
}
