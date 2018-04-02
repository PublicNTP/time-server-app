package org.publicntp.gnssreader.ui.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.preferences.TimezoneStore;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.repository.LocationStorageConsumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * Created by zac on 3/29/18.
 */

public class SettingsDialogFragment extends DialogFragment {
    @BindView(R.id.options_share_location) LinearLayout shareLocationButton;
    @BindView(R.id.options_timezone) Spinner timezoneSpinner;
    @BindView(R.id.options_location_units) Spinner locationSpinner;

    private Activity activity;

    private String[] timezoneChoices;
    private String[] locationChoices;

    public interface OnOptionPicked {
        void onTimezonePicked(String timezone);

        void onLocationPicked(String units);
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

        View rootView = inflater.inflate(R.layout.dialog_options, null);
        ButterKnife.bind(this, rootView);

        timezoneChoices = getResources().getStringArray(R.array.timezone_choices);
        locationChoices = getResources().getStringArray(R.array.location_choices);

        timezoneSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.timezone_choices, android.R.layout.simple_spinner_item));
        locationSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.location_choices, android.R.layout.simple_spinner_item));

        setSpinnerSelection(new TimezoneStore().get(activity), timezoneChoices, timezoneSpinner);
        //TODO set selection for location spinner
        //setSpinnerSelection(new TimezoneStore().get(activity), timezoneChoices, timezoneSpinner);

        //builder.setPositiveButton("Done", (dialog, which) -> {
        //})
        builder.setView(rootView);
        return builder.create();
    }

    @OnItemSelected(R.id.options_location_units)
    public void onLocationUnitsClicked(AdapterView<?> parent, View view, int position, long id) {
        String units = locationChoices[position];
        onOptionPicked.onLocationPicked(units);
    }

    @OnItemSelected(R.id.options_timezone)
    public void onTimezoneClicked(AdapterView<?> parent, View view, int position, long id) {
        String timezone = timezoneChoices[position];
        new TimezoneStore().set(activity, timezone);
        onOptionPicked.onTimezonePicked(timezone);
    }

    @OnClick(R.id.options_share_location)
    public void shareLocation() {
        if(LocationStorage.isPopulated()) {
            Uri coordinateUri = Uri.parse(new LocationStorageConsumer().getSharableLocation());
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, coordinateUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, new LocationStorageConsumer().getSharableLocation());
            startActivity(shareIntent);
        } else {
            Snackbar.make(shareLocationButton, "No Location Found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.options_copy_to_clipboard)
    public void copyLocation() {
        if(LocationStorage.isPopulated()) {
            String location = new LocationStorageConsumer().getHumanReadableLocation();
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Location", location));
            Snackbar.make(shareLocationButton, "Copied.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(shareLocationButton, "No Location Found", Snackbar.LENGTH_SHORT).show();
        }
    }
}
