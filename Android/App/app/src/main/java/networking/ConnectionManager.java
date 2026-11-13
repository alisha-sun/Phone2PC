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


    private static volatile Socket pcSocket = null;

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

    public static boolean listenForResponse() {
        pcSocket = null;
        AtomicBoolean bool = new AtomicBoolean(false);
        ArrayList<Socket> sockets = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true) {
                try {System.out.println("Waiting...");
                    Socket client = server.accept();
                    sockets.add(client); System.out.println("Client accepted.");
                    Thread thread = new Thread(() -> handshake(client, server, bool));
                    thread.start();
                } catch (SocketException e) { //server closed by winning thread
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
}
