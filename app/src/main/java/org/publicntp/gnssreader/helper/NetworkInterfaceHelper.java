package org.publicntp.gnssreader.helper;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkInterfaceHelper {
    private List<NetworkInterface> availableInterfaces;

    public NetworkInterfaceHelper() {
        availableInterfaces = allInterfaces();
    }

    private List<String> availableInterfaceNames() {
        return availableInterfaces.stream().map(NetworkInterface::getDisplayName).collect(Collectors.toList());
    }

    public List<NetworkInterface> allInterfaces() {
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            while(networkInterfaceEnumeration.hasMoreElements()) {
                networkInterface = networkInterfaceEnumeration.nextElement();
                if(networkInterface.isVirtual()) continue;
                if(!networkInterface.isUp()) continue;
                networkInterfaces.add(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return networkInterfaces;
    }

    public boolean hasInterface(String interfaceName) {
        return availableInterfaceNames().contains(interfaceName);
    }

    public NetworkInterface byName(String name) {
        return availableInterfaces.stream().filter(i -> i.getDisplayName().equals(name)).collect(Collectors.toList()).get(0);
    }

    public boolean hasConnectivityOn(String name) {
        if(hasInterface(name)) {
            NetworkInterface networkInterface = byName(name);
            boolean hasAddress = networkInterface.getInetAddresses().hasMoreElements();
            return hasAddress;
        } else {
            return false;
        }
    }

    public List<Boolean> hasInterfaces(List<String> interfaceNames) {
        return interfaceNames.stream().map(this::hasInterface).collect(Collectors.toList());
    }

    public boolean hasConnectivityOnAnyOf(List<String> interfaceNames) {
        return interfaceNames.stream().filter(this::hasInterface).anyMatch(this::hasConnectivityOn);
    }

    public boolean hasAnyOf(List<String> interfaceNames) {
        return interfaceNames.stream().anyMatch(this::hasInterface);
    }

    public String ipFor(String interfaceName) {
        if(hasInterface(interfaceName)) {
            NetworkInterface networkInterface = byName(interfaceName);
            Enumeration<InetAddress> addressEnumerator = networkInterface.getInetAddresses();
            while(addressEnumerator.hasMoreElements()) {
                InetAddress address = addressEnumerator.nextElement();
                if(address instanceof Inet6Address) {
                    continue;
                }
                return address.getHostAddress();
            }
        }
        return "0.0.0.0";
    }
}
