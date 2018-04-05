package org.publicntp.gnssreader.helper;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import org.publicntp.gnssreader.R;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zac on 3/20/18.
 */

public class GreyLevelHelper {
    private static TreeMap<Integer, Integer> greyLevels = new TreeMap<>();
    private static TreeMap<Integer, Paint> greyPaintLevels = new TreeMap<>();

    static {
        greyLevels.put(Integer.MIN_VALUE, R.color.greylight);
        greyLevels.put(5, R.color.grey);
        greyLevels.put(15, R.color.greydark);
        greyLevels.put(20, R.color.black);
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

    public static int asId(float f) {
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
