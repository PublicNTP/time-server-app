package app.timeserver.service.ntp.logging;

import android.support.annotation.NonNull;

import java.net.DatagramPacket;

/**
 * Created by zac on 4/2/18.
 */

public class ServerLogDataPoint implements Comparable<ServerLogDataPoint> {
    public final DatagramPacket packet;
    public final long timeReceived;
    public final boolean isInbound;

    public ServerLogDataPoint(long timeReceived, DatagramPacket packet, boolean isInbound) {
        this.timeReceived = timeReceived;
        this.isInbound = isInbound;
        this.packet = packet;
    }

    @Override
    public int compareTo(@NonNull ServerLogDataPoint o) {
        return Long.compare(this.timeReceived, o.timeReceived);
    }
}
