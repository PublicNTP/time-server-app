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
    @BindView(R.id.time_zone_spinner) Spinner timeZoneSpinner;
    //@BindView(R.id.time_zone_switch) Switch switchButton;

    private Activity activity;

    private String[] measurementChoices;
    private String[] locationChoices;
    private String[] timeZoneChoices;

    public interface OnOptionPicked {
      void onLocationPicked(Integer units);
      void onMeasurementPicked(Integer measurement);
      void onTimezonePicked(Integer timezone);
    }

    private OnOptionPicked onOptionPicked;

    public void setOnOptionPicked(OnOptionPicked onOptionPicked) {
        this.onOptionPicked = onOptionPicked;
    }

    public void setSpinnerSelection(Integer selected, String[] choices, Spinner spinner) {
      spinner.setSelection(selected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_options, null);
        ButterKnife.bind(this, rootView);

        measurementChoices = getResources().getStringArray(R.array.measurement_choices);
        locationChoices = getResources().getStringArray(R.array.location_choices);
        timeZoneChoices = getResources().getStringArray(R.array.timezone_choices);

        measurementSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.measurement_choices, R.layout.spinner_item));
        locationSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.location_choices, R.layout.spinner_item));
        timeZoneSpinner.setAdapter(ArrayAdapter.createFromResource(activity, R.array.timezone_choices, R.layout.spinner_item));


        setSpinnerSelection(new MeasurementStore().get(activity, measurementChoices), measurementChoices, measurementSpinner);
        setSpinnerSelection(new LocationCoordinateTypeStore().get(activity, locationChoices), locationChoices, locationSpinner);
        setSpinnerSelection(new TimezoneStore().get(activity, timeZoneChoices), timeZoneChoices, timeZoneSpinner);

        builder.setView(rootView);
        return builder.create();
    }

    @OnItemSelected(R.id.options_location_units)
    public void onLocationUnitsClicked(AdapterView<?> parent, View view, int position, long id) {
        String units = locationChoices[position];
        new LocationCoordinateTypeStore().set(activity, position);
        onOptionPicked.onLocationPicked(position);
    }

    @OnItemSelected(R.id.options_measurement)
    public void onMeasurementClicked(AdapterView<?> parent, View view, int position, long id) {
        String measurement = measurementChoices[position];
        new MeasurementStore().set(activity, position);
        onOptionPicked.onMeasurementPicked(position);
    }

    @OnItemSelected(R.id.time_zone_spinner)
    public void onTimeZoneSelected(AdapterView<?> parent, View view, int position, long id) {
        String timezone = timeZoneChoices[position];
        new TimezoneStore().set(activity, position);
        onOptionPicked.onTimezonePicked(position);
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
            CoordinateConverter converter = new LocationCoordinateTypeStore().getConverter(getContext(), locationChoices);
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
