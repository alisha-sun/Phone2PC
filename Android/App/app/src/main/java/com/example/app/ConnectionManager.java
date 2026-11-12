package com.example.app;
import java.net.Socket;
import java.io.OutputStream;
public class ConnectionManager {
    public static boolean connectToPC(String host){
        try {
            System.out.println("TryConnect: " + host);
            Socket socket = new Socket(host, 5000);
            OutputStream out = socket.getOutputStream();

            byte[] data = new byte[4096];

            out.write(data);
            out.flush();

            System.out.println("Sent 4096 bytes");

            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
