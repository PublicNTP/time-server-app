package org.publicntp.timeserver.service.ntp.logging;

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

    public long totalCount() {
        return inbound + outbound;
    }

}
