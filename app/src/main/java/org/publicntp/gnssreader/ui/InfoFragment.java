package org.publicntp.gnssreader.ui;


import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.publicntp.gnssreader.R;

/**
 * Simple Fragment to render info view.
 *
 * The info view contains mostly static information with
 * a few dynamic values that contain app related values.
 *
 * The dynamic values should never change during runtime.
 *
 * @author Richard Macdonald <richard@thewidgetsmith.com>
 */
public class InfoFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        TextView textVersion = view.findViewById(R.id.info_text_version);
        TextView textDeveloper = view.findViewById(R.id.info_text_developer);
        TextView textOrganization = view.findViewById(R.id.info_text_organization);

        Button contactButton = view.findViewById(R.id.info_btn_contact);
        Button websiteButton = view.findViewById(R.id.info_btn_website);

        textVersion.setText(getTextVersion());
        textDeveloper.setText(getTextDeveloper());
        textOrganization.setText(getTextOrganization());

        contactButton.setOnClickListener(this);
        websiteButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        Activity activity = this.getActivity();

        switch (view.getId()) {
            case R.id.info_btn_contact:
                Toast.makeText(activity, "Contact Button Tapped", Toast.LENGTH_LONG).show();
                break;

            case R.id.info_btn_website:
                Toast.makeText(activity, "Website Button Tapped", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private String getTextVersion() {
        String appVersion = getAppVersion();
        String template = getString(R.string.info_text_version);
        return String.format(template, appVersion);
    }

    private String getTextDeveloper() {
        String devName = getString(R.string.developer_name);
        String template = getString(R.string.info_text_developer);
        return String.format(template, devName);
    }

    private String getTextOrganization() {
        String orgName = getString(R.string.organization_name);
        String template = getString(R.string.info_text_organization);
        return String.format(template, orgName);
    }

    private String getAppVersion() {

        String versionName;

        try {

            Activity activity = this.getActivity();
            PackageInfo packageInfo = activity
                                        .getPackageManager()
                                        .getPackageInfo(activity.getPackageName(), 0);

            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException ex) {
            // This is not critical functionality so, if it
            // fails, ignore ex and display "NaN" on the UI.
            versionName = "NaN";
        }

        return versionName;
    }

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }
}
