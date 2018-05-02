package com.berico.coords;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;

public class Coordinates {
	
	public static String mgrsFromLatLon(double lat, double lon){
		
		Angle latitude = Angle.fromDegrees(lat);
		
		Angle longitude = Angle.fromDegrees(lon);
		
		return MGRSCoord
				.fromLatLon(latitude, longitude)
				.toString();
	}
	
	public static double[] latLonFromMgrs(String mgrs){
		
		MGRSCoord coord = MGRSCoord.fromString(mgrs);
		
		return new double[]{ 
			coord.getLatitude().degrees, 
			coord.getLongitude().degrees 
		};
	}

}