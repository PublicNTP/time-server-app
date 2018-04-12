package org.publicntp.gnssreader.service.ntp.log;

import java.net.DatagramPacket;

/**
 * Created by zac on 4/2/18.
 */

public class ServerLogDataPoint {
    public final DatagramPacket packet;
    public final long timeReceived;
    public final boolean isInbound;

    public ServerLogDataPoint(long timeReceived, DatagramPacket packet, boolean isInbound) {
        this.timeReceived = timeReceived;
        this.isInbound = isInbound;
        this.packet = packet;
    }
}
