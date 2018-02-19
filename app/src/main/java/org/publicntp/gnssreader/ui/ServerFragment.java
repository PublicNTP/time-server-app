package org.publicntp.gnssreader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.RootChecker;


public class ServerFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!RootChecker.isRootGiven()) {
            Toast.makeText(getContext(), "This feature is only available with a rooted device.", Toast.LENGTH_LONG).show();
        }
    }

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }
}
