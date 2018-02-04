package org.publicntp.gnssreader.ui.chart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by richard on 2/3/18.
 */

public class SatellitePointValueFormatter implements IValueFormatter {

    /**
     * DecimalFormat for formatting
     */
    protected DecimalFormat mFormat;

    protected int mDecimalDigits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public SatellitePointValueFormatter(int digits) {

        //setup(digits);

        mFormat = new DecimalFormat("##0");
    }

    /**
     * Sets up the formatter with a given number of decimal digits.
     *
     * @param digits
     */
    public void setup(int digits) {

        this.mDecimalDigits = digits;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        // put more logic here ...
        // avoid memory allocations here (for performance reasons)

        //return mFormat.format(value);

        return String.valueOf((int) value);
    }

    /**
     * Returns the number of decimal digits this formatter uses.
     *
     * @return
     */
    public int getDecimalDigits() {
        return mDecimalDigits;
    }
}
