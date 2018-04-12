package org.publicntp.gnssreader.helper;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;

public class Winebar {
    public static Snackbar make(View view, String text, int length, int color) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        snackbar.getView().setBackgroundColor(color);
        return snackbar;
    }

    public static Snackbar make(View view, String text, int length) {
        return make(view, text, length, ContextCompat.getColor(view.getContext(), R.color.blue));
    }

    public static Snackbar make(View view, int text_id, int length) {
        String text = view.getContext().getString(text_id);
        return make(view, text, length, ContextCompat.getColor(view.getContext(), R.color.blue));
    }
}
