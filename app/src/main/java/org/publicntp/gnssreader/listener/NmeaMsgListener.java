package org.publicntp.gnssreader.listener;

import android.location.OnNmeaMessageListener;

import org.publicntp.gnssreader.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TerryPC on 11/12/2017.
 */

public class NmeaMsgListener implements OnNmeaMessageListener {
    MainActivity mMainActivity;
    List<String> mNmeaCmdsSeen;

    public NmeaMsgListener(MainActivity activity) {
        mMainActivity = activity;
        mNmeaCmdsSeen = new ArrayList<String>();
    }

    public void onNmeaMessage(String message, long timestamp) {
//        mMainActivity.displayMsg( "Received message " + message + " at timestamp " +
//            timestamp );

        // Parse NMEA message
        NmeaMsg nmeaMsg = new NmeaMsg(message);

        boolean cmdSeen = false;
        for (int i = 0; i < mNmeaCmdsSeen.size(); ++i) {
            if (nmeaMsg.getNmeaCmd().equals(mNmeaCmdsSeen.get(i))) {
                cmdSeen = true;
                break;
            }
        }

        if (cmdSeen == false) {
            mNmeaCmdsSeen.add(nmeaMsg.getNmeaCmd());
            Collections.sort(mNmeaCmdsSeen);

            String displayString = "NMEA cmds seen:\n\n";

            for (int i = 0; i < mNmeaCmdsSeen.size(); ++i) {
                displayString += mNmeaCmdsSeen.get(i) + "\n";
            }

            mMainActivity.displayMsg(displayString);
        }
    }

    public class NmeaMsg {

        protected String mFullNmeaString;
        protected String mNmeaCmd;
        protected String mUtcTime;
        protected String mLat;
        protected String mLatRef;
        protected String mLon;
        protected String mLonRef;
        protected String mFixMode;
        protected String mSatUsed;
        protected String mHdop;
        protected String mAlt;
        protected String mAltUnit;
        protected String mGeo;
        protected String mGeoUnit;
        protected String mDgpsAge;
        protected String mDpgsRef;
        protected String mChecksum;


        public NmeaMsg(String nmeaMessage) {
            mFullNmeaString = nmeaMessage;

            // Split by commas
            String[] nmeaFields = nmeaMessage.split(",");
            int i = 0;
            mNmeaCmd = nmeaFields[i++];
            mUtcTime = nmeaFields[i++];
            /*
            mLat = nmeaFields[i++].substring(0, 2);
            mLat = parseNmeaLatitude( nmeaFields[i++] );
            mLatRef = nmeaFields[i++];
            mLon = nmeaFields[i++];
            mLonRef = nmeaFields[i++];
            mFixMode = nmeaFields[i++];
            mSatUsed = nmeaFields[i++];
            mHdop = nmeaFields[i++];
            mAlt = nmeaFields[i++];
            mAltUnit = nmeaFields[i++];
            mGeo = nmeaFields[i++];
            mGeoUnit = nmeaFields[i++];
            mDgpsAge = nmeaFields[i++];
            mDpgsRef = nmeaFields[i++];
            mChecksum = nmeaFields[i];
            */
        }

        @Override
        public String toString() {
            return "Cmd: " + mNmeaCmd + ", UTC Time: " + mUtcTime;
        }


        protected float parseNmeaLatitude(String latitude) {
            if (latitude.equals("")) {
                return 0.0f;
            }
            // Comes in as "ddmm.ffff" where:
            //  dd = integer degrees
            //  mm = integer minutes
            //  ffff = fractional minutes

            float degrees = 0f;

            try {
                degrees = latitude.length();
                //degrees = 8f;
            } catch (NumberFormatException e) {
                degrees = 7777.1f;
            } catch (NullPointerException e) {
                degrees = 88888.1f;
            }


            return degrees;

            /*
            float fractionalDegrees = ( Float.parseFloat(latitude.substring(2, 4)) +
                    Float.parseFloat("0" + latitude.substring(4)) / 60.0f );

            return Float.parseFloat(latitude.substring(0,2)) + fractionalDegrees;
            */
        }

        public String getNmeaCmd() {
            return mNmeaCmd;
        }

        public String getUtcTime() {
            return mUtcTime;
        }
    }
}
