package org.publicntp.gnssreader.service.ntp;

/**
 * Created by zac on 4/2/18.
 */

public class ServerLogDataPoint {
    public long timeReceived;
    public int numberReceived;

    public ServerLogDataPoint(long timeReceived, int numberReceived) {
        this.timeReceived = timeReceived;
        this.numberReceived = numberReceived;
    }
}
