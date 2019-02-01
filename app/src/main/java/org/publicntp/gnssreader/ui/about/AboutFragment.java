package org.publicntp.timeserver.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.publicntp.timeserver.BuildConfig;
import org.publicntp.timeserver.R;
import org.publicntp.timeserver.ui.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutFragment extends BaseFragment {
    @BindView(R.id.about_version) TextView versionView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        versionView.setText(String.format(getString(R.string.about_text_version), BuildConfig.VERSION_NAME));

        return view;
    }

    @OnClick(R.id.donate_btn)
    public void websiteClick() {
        launchWebUrl(getString(R.string.owner_donate_address));
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
    }
}
