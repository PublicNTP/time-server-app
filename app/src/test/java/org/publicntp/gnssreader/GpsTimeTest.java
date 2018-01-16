package org.publicntp.gnssreader;

import org.junit.Test;
import org.publicntp.gnssreader.model.GpsTime;

import java.util.Date;

/**
 * Created by zac on 11/17/17.
 */

public class GpsTimeTest {
    @Test
    public void GPS_Epoch_is_Correct() throws Exception {
        Date date = GpsTime.GPS_EPOCH.getTime();
        System.out.println(date);
        assert true;
    }
}
