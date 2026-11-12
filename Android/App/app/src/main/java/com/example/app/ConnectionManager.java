package com.example.app;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class ConnectionManager {
    private static final int PORT = 5001;
    private static final byte[] DATA = "PHONE2PC_DISCOVER".getBytes(StandardCharsets.UTF_8);

    public static int sendBroadcast() {
        Enumeration<NetworkInterface> interfaces;

        try (DatagramSocket socket = new DatagramSocket()) {
            interfaces = NetworkInterface.getNetworkInterfaces();
            socket.setBroadcast(true);

            int count = 0;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcastAddress = address.getBroadcast();
                    if (broadcastAddress instanceof Inet4Address) {
                        DatagramPacket packet = new DatagramPacket(DATA, DATA.length, broadcastAddress, PORT);
                        try { socket.send(packet); count++; }
                        catch (IOException e) { e.printStackTrace(); }
                    }
                }
            }
            return count;
        } catch (SocketException e){
            e.printStackTrace();
            return -1;
        }
    }
}
