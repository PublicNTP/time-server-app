package org.publicntp.gnssreader.service.ntp.log;

import org.publicntp.gnssreader.helper.TimeMillis;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerLogDataPointGrouper {
    private static HashMap<Long, List<ServerLogDataPoint>> allData = new HashMap<>();
    private static Long lastCleaned = System.currentTimeMillis();

    private static void cleanOld() {
        Long oldestAllowed = System.currentTimeMillis() - TimeMillis.HOUR * 3;
        oldestAllowed = asKey(oldestAllowed);
        for(Long key : allData.keySet()) {
            if(key < oldestAllowed) {
                allData.remove(key);
            }
        }
    }

    private static Long asKey(Long millis) {
        return millis / (1000 * 60);
    }

    private static List<ServerLogDataPoint> atMinute(Long millis) {
        return allData.getOrDefault(asKey(millis), new ArrayList<>());
    }

    public synchronized static void addPacket(ServerLogDataPoint serverLogDataPoint) {
        if(System.currentTimeMillis() - lastCleaned > TimeMillis.HOUR) {
            cleanOld();
        }

        Long key = asKey(serverLogDataPoint.timeReceived);
        List<ServerLogDataPoint> listAtTime = allData.getOrDefault(key, new ArrayList<>());
        listAtTime.add(serverLogDataPoint);
        allData.put(key, listAtTime);
    }

    static synchronized long inBoundAtMinute(Long millis) {
        return atMinute(millis).stream().filter(dp -> dp.isInbound).count();
    }

    static synchronized long outBoundAtMinute(Long millis) {
        return atMinute(millis).stream().filter(dp -> !dp.isInbound).count();
    }
}
