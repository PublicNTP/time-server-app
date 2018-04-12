package org.publicntp.gnssreader.service.ntp.log;

public class ServerLogMinuteSummary {
    public final long timeReceived;
    public final long inbound;
    public final long outbound;

    private ServerLogMinuteSummary(long timeReceived, long inbound, long outbound) {
        this.timeReceived = timeReceived;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public long getTotal() {
        return inbound + outbound;
    }

    public static ServerLogMinuteSummary fromGrouper(Long timeReceived) {
        return new ServerLogMinuteSummary(timeReceived,
                ServerLogDataPointGrouper.inBoundAtMinute(timeReceived),
                ServerLogDataPointGrouper.outBoundAtMinute(timeReceived));
    }
}
