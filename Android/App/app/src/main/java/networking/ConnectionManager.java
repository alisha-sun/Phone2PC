package networking;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager {
    private static final int PORT = 5001; //port of ServerSocket on both devices
    private static final byte[] DATA = "PHONE2PC_DISCOVER".getBytes(StandardCharsets.UTF_8);
    private static final byte[] RESPONSE = "PHONE2PC_STOP-YELLING".getBytes(StandardCharsets.UTF_8);

    private static Thread listener = null;
    private static volatile Socket pcSocket = null;


    /* PUBLIC */

    public static void sendBroadcast() {
        new Thread(() -> {
                Enumeration<NetworkInterface> interfaces;
                int count = 0;

                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setBroadcast(true);
                    interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) count += broadcastTo(interfaces.nextElement(), socket);
                } catch (SocketException e) {
                    e.printStackTrace();
                    count = -1;
                }
                System.out.println("Broadcast count: " + count);
            }
        ).start();
    }

    public static void startListening(){
        if (listener != null && listener.isAlive()) return;
        pcSocket = null;
        listener = new Thread(ConnectionManager::listenForResponse);
        listener.start();
    }


    /* PRIVATE */

    private static boolean listenForResponse() {
        try (ServerSocket server = new ServerSocket(PORT)){
            ArrayList<Socket> sockets = new ArrayList<>();
            AtomicBoolean bool = new AtomicBoolean(false);
            while (true) {
                try {                                   System.out.println("Waiting...");
                    Socket client = server.accept();
                    sockets.add(client);                System.out.println("Client accepted.");
                    new Thread(() -> handshake(client, server, bool)).start();
                } catch (SocketException e) {           System.out.println("Server closed.");
                    for (Socket s : sockets) {
                        if (s != pcSocket) try { s.close(); } catch (IOException ignored) {}; //this should kill every thread with SocketException
                    }
                    return pcSocket != null;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void handshake(Socket socket, ServerSocket server, AtomicBoolean bool){
        try {
            InputStream input = socket.getInputStream();
            byte[] buffer = new byte[RESPONSE.length];

            int totalRead = 0;
            while (totalRead < buffer.length) {
                int bytesRead = input.read(buffer, totalRead, buffer.length - totalRead);

                if (bytesRead == -1) break;

                totalRead += bytesRead;
            }

            if (totalRead == RESPONSE.length && Arrays.equals(RESPONSE, buffer)) {
                if (bool.compareAndSet(false, true)) {
                    pcSocket = socket;
                    server.close();
                    return;
                }
            }
            socket.close();
        }catch (SocketException e){
            System.out.println("Thread terminated.");
        }
        catch (IOException e){
            e.printStackTrace();
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private static int broadcastTo(NetworkInterface networkInterface, DatagramSocket socket){
        int count = 0;
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcastAddress = address.getBroadcast();
            if (broadcastAddress instanceof Inet4Address) {
                DatagramPacket packet = new DatagramPacket(DATA, DATA.length, broadcastAddress, PORT);
                try {
                    socket.send(packet);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }
}
