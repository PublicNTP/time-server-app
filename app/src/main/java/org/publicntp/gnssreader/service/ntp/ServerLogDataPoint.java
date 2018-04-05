package org.publicntp.gnssreader.service.ntp;

/**
 * Created by zac on 4/2/18.
 */

public class ServerLogDataPoint {
    public long timeReceived;
    public int numberReceived;
    public int numberSent;

    public ServerLogDataPoint(long timeReceived, int numberReceived) {
        this.timeReceived = timeReceived;
        this.numberReceived = numberReceived / 2;
        this.numberSent = numberReceived / 2;
    }
}
