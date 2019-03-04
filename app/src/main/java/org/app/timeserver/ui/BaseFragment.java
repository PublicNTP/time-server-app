package org.app.timeserver.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}
