package app.timeserver.ui.time;

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

import app.timeserver.R;
import app.timeserver.helper.Winebar;
import app.timeserver.helper.preferences.LocationCoordinateTypeStore;
import app.timeserver.helper.preferences.TimezoneStore;
import app.timeserver.helper.preferences.MeasurementStore;
import app.timeserver.repository.location.LocationStorage;
import app.timeserver.repository.location.LocationStorageConsumer;
import app.timeserver.repository.location.converters.CoordinateConverter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnCheckedChanged;

/**
 * Created by zac on 3/29/18.
 */

public class OptionsDialogFragment extends DialogFragment {
    @BindView(R.id.options_share_location) LinearLayout shareLocationButton;
    @BindView(R.id.options_measurement) Spinner measurementSpinner;
    @BindView(R.id.options_location_units) Spinner locationSpinner;
    @BindView(R.id.time_zone_switch) Switch switchButton;

    private Activity activity;

    private String[] measurementChoices;
    private String[] locationChoices;

    public interface OnOptionPicked {
      void onLocationPicked(String units);
      void onMeasurementPicked(String measurement);
      void onTimezonePicked(String timezone);
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

        String timezone = new TimezoneStore().get(activity);
        boolean local = timezone.equals("Local");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_options, null);
        ButterKnife.bind(this, rootView);

        measurementChoices = getResources().getStringArray(R.array.measurement_choices);
        locationChoices = getResources().getStringArray(R.array.location_choices);

        measurementSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.measurement_choices, R.layout.spinner_item));
        locationSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.location_choices, R.layout.spinner_item));

        setSpinnerSelection(new MeasurementStore().get(activity), measurementChoices, measurementSpinner);
        setSpinnerSelection(new LocationCoordinateTypeStore().get(activity), locationChoices, locationSpinner);

        switchButton.setChecked(local);

        builder.setView(rootView);
        return builder.create();
    }

    @OnItemSelected(R.id.options_location_units)
    public void onLocationUnitsClicked(AdapterView<?> parent, View view, int position, long id) {
        String units = locationChoices[position];
        new LocationCoordinateTypeStore().set(activity, units);
        onOptionPicked.onLocationPicked(units);
    }

    @OnItemSelected(R.id.options_measurement)
    public void onMeasurementClicked(AdapterView<?> parent, View view, int position, long id) {
        String measurement = measurementChoices[position];
        new MeasurementStore().set(activity, measurement);
        onOptionPicked.onMeasurementPicked(measurement);
    }

    @OnCheckedChanged(R.id.time_zone_switch)
    public void onTimezoneClicked() {
        String timezone = switchButton.isChecked() ? "Local" : "UTC";
        String oldzone = new TimezoneStore().get(activity);
        if(oldzone != timezone){
          new TimezoneStore().set(activity, timezone);
          onOptionPicked.onTimezonePicked(timezone);
        }
    }

    @OnClick(R.id.options_share_location)
    public void shareLocation() {
        if(LocationStorage.isPopulated()) {
            Uri coordinateUri = Uri.parse(new LocationStorageConsumer().getSharableLocation());
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, coordinateUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, new LocationStorageConsumer().getSharableLocation());
            shareIntent.setPackage("com.google.android.apps.maps");
            if(shareIntent.resolveActivity(shareLocationButton.getContext().getPackageManager()) != null) {
                startActivity(shareIntent);
            } else {
                Winebar.make(shareLocationButton, "Maps is not installed", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Winebar.make(shareLocationButton, "No Location Found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.options_copy_to_clipboard)
    public void copyLocation() {
        if(LocationStorage.isPopulated()) {
            CoordinateConverter converter = new LocationCoordinateTypeStore().getConverter(getContext());
            String location = new LocationStorageConsumer(converter).getHumanReadableLocation();
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Location", location));
            Winebar.make(shareLocationButton, "Copied.", Snackbar.LENGTH_SHORT).show();
        } else {
            Winebar.make(shareLocationButton, "No Location Found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.options_done)
    public void dismissOptions() {
        this.dismiss();
    }
}
