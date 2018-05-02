package org.publicntp.gnssreader.repository.location.converters;

import com.google.openlocationcode.OpenLocationCode;

public class OLCConverter extends CoordinateConverter {
    @Override
    public String getString(double lat, double lon) {
        return new OpenLocationCode(lat, lon).getCode();
    }
}
