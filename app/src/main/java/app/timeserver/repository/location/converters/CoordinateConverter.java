package app.timeserver.repository.location.converters;

public class CoordinateConverter {
    public static CoordinateConverter byName(Integer name) {
        switch(name) {
            case 0:
                return new LatLongConverter();
            case 1:
                return new UTMConverter();
            case 2:
                return new MGRSConverter();
            case 3:
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
