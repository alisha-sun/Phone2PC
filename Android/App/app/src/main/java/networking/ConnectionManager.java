package networking;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ConnectionManager {
    private static final int PORT = 5001; //port of ServerSocket on both devices
    private static final byte[] DATA = "PHONE2PC_DISCOVER".getBytes(StandardCharsets.UTF_8);
    private static final byte[] RESPONSE = "PHONE2PC_STOP-YELLING".getBytes(StandardCharsets.UTF_8);

    private static ResponseListener listener = null;


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

    public static void startListening(Runnable onSuccess){
        if (listener != null && listener.isAlive()) return;
        listener = new ResponseListener(onSuccess);
        listener.start();
    }


    /* PRIVATE */

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

    private static class ResponseListener extends Thread{
        private ServerSocket server;
        private final AtomicBoolean connectionEstablished;
        private final Runnable onSuccess;

        public ResponseListener(Runnable onSuccess){
            this.onSuccess = onSuccess;
            this.connectionEstablished = new AtomicBoolean(false);
        }

        @Override
        public void start() {
            SessionManager.socket = null;
            super.start();
        }

        @Override
        public void run() {
            try (ServerSocket temp = new ServerSocket(PORT)) {
                server = temp;
                ArrayList<Socket> sockets = new ArrayList<>();
                while (true) {
                    System.out.println("Waiting...");
                    try {
                        Socket client = server.accept();
                        sockets.add(client);
                        System.out.println("Client accepted.");
                        new Thread(() -> handshake(client)).start();
                    } catch (SocketException e) {
                        System.out.println("Server closed.");
                        for (Socket s : sockets) {
                            if (s != SessionManager.socket) try { s.close(); } catch (IOException ignored) {}
                            //this should kill every thread with SocketException
                        }
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); //server failed to start
            }

            if (SessionManager.socket != null) onSuccess.run();
        }

        private void handshake(Socket socket){
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
                    if (connectionEstablished.compareAndSet(false, true)) {
                        SessionManager.socket = socket;
                        server.close();
                        return;
                    }
                }
                socket.close();
            } catch (SocketException e){
                System.out.println("Thread terminated.");
            }
            catch (IOException e){
                e.printStackTrace();
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}
