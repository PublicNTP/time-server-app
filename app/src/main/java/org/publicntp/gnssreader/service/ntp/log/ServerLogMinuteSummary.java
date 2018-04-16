package org.publicntp.gnssreader.service.ntp.log;

import java.util.List;

public class ServerLogMinuteSummary {
    public final long timeReceived;
    public final long inbound;
    public final long outbound;

    public ServerLogMinuteSummary(long timeReceived, List<ServerLogDataPoint> summaries) {
        this.timeReceived = timeReceived;
        this.inbound = summaries.parallelStream().filter(s -> s.isInbound).count();
        this.outbound = summaries.parallelStream().filter(s -> !s.isInbound).count();
    }

    public long getTotal() {
        return inbound + outbound;
    }

}
