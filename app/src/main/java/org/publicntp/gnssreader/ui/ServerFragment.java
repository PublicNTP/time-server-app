package org.publicntp.gnssreader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.RootChecker;
import org.publicntp.gnssreader.service.ntp.NtpService;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServerFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.server_btn_toggle) Button toggleServerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server, container, false);
        ButterKnife.bind(this, rootView);

        toggleServerButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @Override
    public void onClick(View view) {
        if(!RootChecker.isRootGiven()) {
            Toast.makeText(getContext(), R.string.no_root_warning, Toast.LENGTH_LONG).show();
            //return;
        }
        NtpService ntpService = NtpService.getNtpService();
        if(ntpService == null) {
            getActivity().startService(NtpService.ignitionIntent(getContext()));
        } else {
            ntpService.stopSelf();
        }
    }
}
