package org.publicntp.gnssreader;

import com.berico.coords.Coordinates;

import org.junit.Test;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class NasaUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        UTMCoord utmCoord = UTMCoord.fromLatLon(Angle.fromDegreesLatitude(41.7386), Angle.fromDegreesLongitude(-111.8224));

        System.out.println(utmCoord);
    }
}
