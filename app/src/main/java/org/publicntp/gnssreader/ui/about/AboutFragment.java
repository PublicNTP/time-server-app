package org.publicntp.gnssreader.ui.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.publicntp.gnssreader.BuildConfig;
import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.ui.BaseFragment;

import static org.publicntp.gnssreader.TimeServerUtility.getContactEmailAddr;
import static org.publicntp.gnssreader.TimeServerUtility.getContactEmailSubj;
import static org.publicntp.gnssreader.TimeServerUtility.getDeveloperName;
import static org.publicntp.gnssreader.TimeServerUtility.getOwnerWebAddr;
import static org.publicntp.gnssreader.TimeServerUtility.getOwnerName;

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
public class AboutFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        view.findViewById(R.id.info_btn_contact).setOnClickListener(v -> composeContactEmail());
        view.findViewById(R.id.info_btn_website).setOnClickListener(v -> launchOwnerWebsiteUrl());

        TextView textVersion = view.findViewById(R.id.info_text_version);
        TextView textDeveloper = view.findViewById(R.id.info_text_developer);
        TextView textOrganization = view.findViewById(R.id.info_text_organization);

        textVersion.setText(getTextVersion());
        textDeveloper.setText(getTextDeveloper());
        textOrganization.setText(getTextOrganization());

        return view;
    }

    private void composeContactEmail() {
        composeEmail(getContactEmailAddr(), getContactEmailSubj());
    }

    private void launchOwnerWebsiteUrl() {
        launchWebUrl(getOwnerWebAddr());
    }

    private String getTextVersion() {
        return String.format(getString(R.string.info_text_version), getAppVersion());
    }

    private String getTextDeveloper() {
        return String.format(getString(R.string.info_text_developer), getDeveloperName());
    }

    private String getTextOrganization() {
        return String.format(getString(R.string.info_text_organization), getOwnerName());
    }

    private String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
}
