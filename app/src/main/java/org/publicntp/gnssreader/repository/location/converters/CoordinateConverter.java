package org.publicntp.gnssreader.repository.location.converters;

public interface CoordinateConverter {
    public String getString1(double lat, double lon);
    public String getString2(double lat, double lon);
}
