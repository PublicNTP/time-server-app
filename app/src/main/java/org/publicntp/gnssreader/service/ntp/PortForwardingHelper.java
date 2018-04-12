package org.publicntp.gnssreader.service.ntp;

import android.annotation.SuppressLint;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class PortForwardingHelper {
    public static class Commands {
        public static String forwardingAllowedCommand(String networkInterface) {
            return String.format("cat /proc/sys/net/ipv4/conf/%s/forwarding", networkInterface);
        }

        public static boolean forwardingAllowed(String networkInterface) {
            try {
                return Shell.SU.run(Commands.forwardingAllowedCommand(networkInterface)).get(0).equals("1");
            } catch (Exception e) {
                return false;
            }
        }

        public static String enableForwardingCommand(String networkInterface) {
            return String.format("echo '1' | tee /proc/sys/net/ipv4/conf/%s/forwarding", networkInterface);
        }

        public static void enableForwarding(String networkInterface) {
            Shell.SU.run(enableForwardingCommand(networkInterface));
        }

        public static String lsCommand(String dir) {
            return String.format("ls %s", dir);
        }

        public static List<String> ls(String dir) {
            return Shell.SU.run(lsCommand(dir));
        }

        public static List<String> networkInterfaces() {
            return Commands.ls("/proc/sys/net/ipv4/conf/");
        }

        @SuppressLint("DefaultLocale")
        public static String redirectCommand(int sourcePort, int destPort) {
            return String.format("iptables -I PREROUTING -t nat -p udp --dport %d -j REDIRECT --to-port %d", sourcePort, destPort);
        }

        public static List<String> redirect(int sourcePort, int destPort) {
            String command = redirectCommand(sourcePort, destPort);
            return Shell.SU.run(command);
        }
    }


    public static boolean allowAllForwarding() {
        try {
            if(!Shell.SU.available()) return false;

            for(String dir: Commands.networkInterfaces()) {
                if(!Commands.forwardingAllowed(dir)) {
                    Commands.enableForwarding(dir);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean forwardUDPPort(int source, int dest) {
        try {
            List<String> result = Commands.redirect(source, dest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
