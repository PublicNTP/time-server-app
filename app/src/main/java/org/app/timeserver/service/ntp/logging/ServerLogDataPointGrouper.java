package org.app.timeserver.service.ntp.logging;

import org.app.timeserver.helper.TimeMillis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class ServerLogDataPointGrouper {
    private static TreeSet<ServerLogDataPoint> setData = new TreeSet<>();
    private static Long lastCleaned = System.currentTimeMillis();

    private static ServerLogDataPoint lastBefore(TreeSet<ServerLogDataPoint> set, long time) {
        Iterator<ServerLogDataPoint> iter = set.descendingIterator();
        while (iter.hasNext()) {
            ServerLogDataPoint nextPoint = iter.next();
            if (nextPoint.timeReceived < time) {
                return nextPoint;
            }
        }
        return set.first();
    }

    private static ServerLogDataPoint firstAfter(TreeSet<ServerLogDataPoint> set, long time) {
        Iterator<ServerLogDataPoint> iter = set.iterator();
        while (iter.hasNext()) {
            ServerLogDataPoint nextPoint = iter.next();
            if (nextPoint.timeReceived > time) {
                return nextPoint;
            }
        }
        return set.last();
    }

    private static void cleanOld() {
        Long oldestAllowed = System.currentTimeMillis() - TimeMillis.HOUR * 3;
        for (ServerLogDataPoint point : setData) {
            if (point.timeReceived >= oldestAllowed) {
                setData.remove(point);
            }
        }
    }

    private static List<ServerLogDataPoint> inRange(long startMillis, long endMillis) {
        if (setData.isEmpty()) return new ArrayList<>();
        TreeSet<ServerLogDataPoint> tailData = (TreeSet<ServerLogDataPoint>) setData.tailSet(lastBefore(setData, startMillis), false);

        if (tailData.isEmpty()) return new ArrayList<>();
        TreeSet<ServerLogDataPoint> croppedData = (TreeSet<ServerLogDataPoint>) tailData.headSet(firstAfter(tailData, endMillis));

        return new ArrayList<>(croppedData);
    }

    public synchronized static void addPacket(ServerLogDataPoint serverLogDataPoint) {
        // Removed because it was causing crash after an hour. 
        // if ((System.currentTimeMillis() + 60) - lastCleaned > ( 60 + TimeMillis.HOUR)) {
        //     cleanOld();
        // }

        setData.add(serverLogDataPoint);
    }

    private static ServerLogMinuteSummary fromGrouper(Long timeReceived) {
        return new ServerLogMinuteSummary(timeReceived, inRange(timeReceived - TimeMillis.MINUTE, timeReceived));
    }

    public synchronized static ServerLogMinuteSummary mostRecent() {
        return fromGrouper(System.currentTimeMillis());
    }

    public synchronized static List<ServerLogMinuteSummary> oneHourSummary() {
        List<ServerLogMinuteSummary> logData = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        startTime = startTime - (startTime % TimeMillis.MINUTE) + TimeMillis.MINUTE; //The end of the current minute

        for (long l = startTime - TimeMillis.HOUR; l <= startTime; l += TimeMillis.MINUTE) {
            logData.add(fromGrouper(l));
        }
        return logData;
    }
}
