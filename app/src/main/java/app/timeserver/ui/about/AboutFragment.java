package app.timeserver.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.timeserver.helper.Winebar;
import android.support.design.widget.Snackbar;

import app.timeserver.BuildConfig;
import app.timeserver.R;
import app.timeserver.ui.BaseFragment;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutFragment extends BaseFragment {
    @BindView(R.id.about_version) TextView versionView;
    @BindView(R.id.myCoordinatorLayout) RelativeLayout viewPos;
    @BindColor(R.color.white) int white;
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
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {

                  Winebar.make(viewPos, R.string.donate_text, Snackbar.LENGTH_INDEFINITE).setAction("VISIT", v -> {
                      launchWebUrl(getString(R.string.owner_visit_address));
                  }).setActionTextColor(white).show();
                }
            },
        3000);
        return view;
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
