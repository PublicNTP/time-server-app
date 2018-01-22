package org.publicntp.gnssreader.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.publicntp.gnssreader.R;


public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void composeEmail(String address, String subject) {
        composeEmail(new String[] { address }, subject);
    }

    protected void composeEmail(String[] addresses, String subject) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {
            startActivity(Intent.createChooser(
                    intent, getString(R.string.chooser_app_email)));

        } catch (ActivityNotFoundException ex) {
            Toast.makeText(
                    getContext(),
                    getString(R.string.error_email_client_not_found),
                    Toast.LENGTH_SHORT).show();
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
    }

    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
