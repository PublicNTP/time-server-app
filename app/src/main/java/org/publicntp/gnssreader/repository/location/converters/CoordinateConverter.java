package org.publicntp.gnssreader.repository.location.converters;

public class CoordinateConverter {
    public static CoordinateConverter byName(String name) {
        switch(name) {
            case "Lat/Long":
                return new LatLongConverter();
            case "UTM":
                return new UTMConverter();
            case "MGRS":
                return new MGRSConverter();
            case "OLC (Plus Codes)":
                return new OLCConverter();
            default:
                throw new RuntimeException("Invalid Coordinate System");
        }
    }

    public String getString(double lat, double lon) {
        throw new RuntimeException("Unimplemented");
    }

    public String clipboardString(double lat, double lon) {
        throw new RuntimeException("Unimplemented");
    }
}
