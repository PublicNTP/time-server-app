package app.timeserver.model;

import android.location.GnssStatus;
import android.util.Log;
import android.os.Build.VERSION;

/**
 * Created by zac on 2/14/18.
 */

public class SatelliteModel {
    public final int constellationType;
    public final int svn;
    public final float snrInDb;
    public final float Cn0DbHz;
    public final float elevationDegrees;
    public final float azimuthDegrees;
    public final boolean ephemerisData;
    public final boolean almanacData;
    public final boolean usedInFix;
    public float carrierFrequencyMhz;
    public boolean hasFrequency = false;


    public SatelliteModel(int i, GnssStatus status) {
        svn = status.getSvid(i);
        constellationType = status.getConstellationType(i);
        Cn0DbHz = status.getCn0DbHz(i);
        elevationDegrees = status.getElevationDegrees(i);
        azimuthDegrees = status.getAzimuthDegrees(i);
        ephemerisData = status.hasEphemerisData(i);
        almanacData = status.hasAlmanacData(i);
        usedInFix = status.usedInFix(i);
        snrInDb =  status.getCn0DbHz(i);
        if(android.os.Build.VERSION.SDK_INT >= 27){
          hasFrequency = status.hasCarrierFrequencyHz(i);
          if(hasFrequency){
            carrierFrequencyMhz = toMhz(status.getCarrierFrequencyHz(i));
          }
        }

    }

    public String constellationName() {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "BEIDOU";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "GALILEO";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "GLONASS";
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            case GnssStatus.CONSTELLATION_SBAS:
                return "SBAS";
            case GnssStatus.CONSTELLATION_UNKNOWN:
            default:
                return "UNKNOWN";
        }
    }
  public String getCarrierFrequencyLabel() {
        final float TOLERANCE_MHZ = 1f;
        String gnssType = constellationName();
        switch (gnssType) {
            case "GPS":
                if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                    return "L1";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1227.6f, TOLERANCE_MHZ)) {
                    return "L2";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1381.05f, TOLERANCE_MHZ)) {
                    return "L3";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1379.913f, TOLERANCE_MHZ)) {
                    return "L4";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                    return "L5";
                }
                break;
            case "GLONASS":
                if (carrierFrequencyMhz >= 1598.0000f && carrierFrequencyMhz <= 1610.000f) {
                    // Actual range is 1598.0625 MHz to 1609.3125, but allow padding for float comparisons - #103
                    return "L1";
                } else if (carrierFrequencyMhz >= 1242.0000f && carrierFrequencyMhz <= 1252.000f) {
                    // Actual range is 1242.9375 - 1251.6875, but allow padding for float comparisons - #103
                    return "L2";
                } else if (carrierFrequencyMhz >= 1200.0000f && carrierFrequencyMhz <= 1210.000f) {
                    // Exact range is unclear - appears to be 1202.025 - 1207.14 - #103
                    return "L3";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                    return "L5";
                }
                break;
            case "BEIDOU":
                if (fuzzyEquals(carrierFrequencyMhz, 1561.098f, TOLERANCE_MHZ)) {
                    return "B1";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1589.742f, TOLERANCE_MHZ)) {
                    return "B1-2";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                    return "B1C";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1207.14f, TOLERANCE_MHZ)) {
                    return "B2";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                    return "B2a";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1268.52f, TOLERANCE_MHZ)) {
                    return "B3";
                }
                break;
            case "QZSS":
                if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                    return "L1";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1227.6f, TOLERANCE_MHZ)) {
                    return "L2";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                    return "L5";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1278.75f, TOLERANCE_MHZ)) {
                    return "L6";
                }
                break;
            case "GALILEO":
                if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                    return "E1";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1191.795f, TOLERANCE_MHZ)) {
                    return "E5";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                    return "E5a";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1207.14f, TOLERANCE_MHZ)) {
                    return "E5b";
                } else if (fuzzyEquals(carrierFrequencyMhz, 1278.75f, TOLERANCE_MHZ)) {
                    return "E6";
                }
                break;
            case "SBAS":
                if (svn == 120 || svn == 123 || svn == 126 || svn == 136) {
                    // EGNOS - https://gssc.esa.int/navipedia/index.php/EGNOS_Space_Segment
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                        return "L5";
                    }
                } else if (svn == 129 || svn == 137) {
                    // MSAS (Japan) - https://gssc.esa.int/navipedia/index.php/MSAS_Space_Segment
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                        return "L5";
                    }
                } else if (svn == 127 || svn == 128 || svn == 139) {
                    // GnssType.GAGAN (India)
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    }
                } else if (svn == 133) {
                    // GnssType.INMARSAT_4F3;
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                        return "L5";
                    }
                } else if (svn == 135) {
                    // GnssType.GALAXY_15;
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                        return "L5";
                    }
                } else if (svn == 138) {
                    // GnssType.ANIK;
                    if (fuzzyEquals(carrierFrequencyMhz, 1575.42f, TOLERANCE_MHZ)) {
                        return "L1";
                    } else if (fuzzyEquals(carrierFrequencyMhz, 1176.45f, TOLERANCE_MHZ)) {
                        return "L5";
                    }
                }
                break;
            case "UNKNOWN":
                break;
            default:
                break;
        }
        // Unknown carrier frequency for given constellation and svn
        return null;
    }
    /**
       * Converts the provided number in Hz to MHz
       * @param hertz value to be converted
       * @return value converted to MHz
       */
      public float toMhz(float hertz) {
          return hertz / 1000000.00f;
      }


      public boolean fuzzyEquals(double a, double b, double tolerance) {
          checkNonNegative("tolerance", tolerance);
          return Math.copySign(a - b, 1.0) <= tolerance
                  // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
                  || (a == b) // needed to ensure that infinities equal themselves
                  || (Double.isNaN(a) && Double.isNaN(b));
      }

      public double checkNonNegative(String role, double x) {
          if (!(x >= 0)) { // not x < 0, to work with NaN.
              throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
          }
          return x;
      }
}
