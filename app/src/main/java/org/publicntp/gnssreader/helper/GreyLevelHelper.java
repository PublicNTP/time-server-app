package org.publicntp.gnssreader.helper;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import org.publicntp.gnssreader.R;

import java.util.TreeMap;

/**
 * Created by zac on 3/20/18.
 */

public class GreyLevelHelper {
    private static TreeMap<Integer, Integer> greyLevels = new TreeMap<>();
    private static TreeMap<Integer, Paint> greyPaintLevels = new TreeMap<>();

    static {
        greyLevels.put(Integer.MIN_VALUE, R.color.greylight);
        greyLevels.put(5, R.color.grey9);
        greyLevels.put(8, R.color.grey8);
        greyLevels.put(11, R.color.grey7);
        greyLevels.put(14, R.color.grey6);
        greyLevels.put(16, R.color.grey5);
        greyLevels.put(19, R.color.grey4);
        greyLevels.put(22, R.color.grey3);
        greyLevels.put(25, R.color.grey2);
        greyLevels.put(28, R.color.grey1);
        greyLevels.put(31, R.color.grey0);
    }

    private static Integer matchingKey(float f) {
        Integer chosenKey = null;
        for (int key : greyLevels.keySet()) {
            if (f >= key) {
                chosenKey = key;
            } else {
                break;
            }
        }
        return chosenKey;
    }

    private static int asId(float f) {
        Integer chosenKey = matchingKey(f);
        if (chosenKey == null) {
            return R.color.greylight;
        } else {
            return greyLevels.get(chosenKey);
        }
    }

    public static int asColor(Context context, float f) {
        return ContextCompat.getColor(context, asId(f));
    }

    public static Paint asPaint(Context context, float f) {
        Integer chosenKey = matchingKey(f);
        if(greyPaintLevels.containsKey(chosenKey)) {
            return greyPaintLevels.get(chosenKey);
        } else {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(asColor(context, f));
            greyPaintLevels.put(chosenKey, paint);
            return paint;
        }
    }
}
