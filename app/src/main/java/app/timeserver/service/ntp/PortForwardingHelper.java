package app.timeserver.service.ntp;

import android.annotation.SuppressLint;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class PortForwardingHelper {
    static class Commands {
        static String forwardingAllowedCommand(String networkInterface) {
            return String.format("cat /proc/sys/net/ipv4/conf/%s/forwarding", networkInterface);
        }

        static boolean forwardingAllowed(String networkInterface) {
            try {
                return Shell.SU.run(Commands.forwardingAllowedCommand(networkInterface)).get(0).equals("1");
            } catch (Exception e) {
                return false;
            }
        }

        static String enableForwardingCommand(String networkInterface) {
            return String.format("echo '1' | tee /proc/sys/net/ipv4/conf/%s/forwarding", networkInterface);
        }

        static void enableForwarding(String networkInterface) {
            Shell.SU.run(enableForwardingCommand(networkInterface));
        }

        static String lsCommand(String dir) {
            return String.format("ls %s", dir);
        }

        static List<String> ls(String dir) {
            return Shell.SU.run(lsCommand(dir));
        }

        static List<String> networkInterfaces() {
            return Commands.ls("/proc/sys/net/ipv4/conf/");
        }

        @SuppressLint("DefaultLocale")
        static String redirectCommand(int sourcePort, int destPort) {
            return String.format("iptables -I PREROUTING -t nat -p udp --dport %d -j REDIRECT --to-port %d", sourcePort, destPort);
        }

        static List<String> redirect(int sourcePort, int destPort) {
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
            Commands.redirect(source, dest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
